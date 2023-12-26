package org.elyte.worker;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.elyte.booking.BookingHandler;
import org.elyte.enums.State;
import org.elyte.enums.WorkerType;
import org.elyte.queue.Queue;
import org.elyte.queue.QueueItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import org.elyte.util.AppConfig;
import org.elyte.util.UtilityFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
@Getter
@AllArgsConstructor
public class BaseWorker extends AppConfig {

    private WorkerType workerType;
    private String QUEUE_NAME = "BOOKING";
    private String EXCHANGE_NAME;
    private String ROUTING_KEY_NAME = "booking";

    private static final Logger log = LoggerFactory.getLogger(BaseWorker.class);

    public BaseWorker() {
        this.EXCHANGE_NAME = this.getConfigValue("EXCHANGE_NAME");

    }

    public CreateWorker createWorker() {
        CreateWorker worker = new CreateWorker(UUID.randomUUID().toString(), UtilityFunctions.timeNow(),
                this.workerType,
                this.QUEUE_NAME);
        return worker;
    }

    private final Connection dbConnection() {

        try {
            Class.forName(this.getConfigValue("JDBC_DRIVER"));
        } catch (final ClassNotFoundException e) {
            System.err.println("ERROR " + e);
        }
        try {
            final Connection conn = DriverManager.getConnection(this.getConfigValue("DB_URL"));
            return conn;
        } catch (SQLException ex) {
            log.error("SQLException: " + ex.getMessage());
            log.error("SQLState: " + ex.getSQLState());
            log.error("VendorError: " + ex.getErrorCode());
            return null;
        }
    }

    public void insertWorkerToDb(CreateWorker worker, Connection conn) throws Exception {
        String sql = " insert into workers (worker_id, created, worker_type, queue_name)"
                + " values (?, ?, ?, ?)";
        try (PreparedStatement preparedStmt = conn.prepareStatement(sql)) {
            preparedStmt.setString(1, worker.getWid());
            preparedStmt.setString(2, worker.getCreated());
            preparedStmt.setString(3, worker.getWorkerType().toString());
            preparedStmt.setString(4, worker.getQueueName());
            int rowsInserted = preparedStmt.executeUpdate();
            log.info("NUMBER OF ROWS INSERTED: " + rowsInserted);
        }
    }

    public void updateONGoingTaskStatusInDb(String tid) throws Exception {
        String sql = "UPDATE tasks SET state=?,started_at=? WHERE task_id=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setString(1, State.PENDING.name());
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, tid);
            preparedStmt.executeUpdate();

        }

    }

    public void updateFinishedTaskInDb(String tid, boolean is_successful, Map<String, Object> taskResult) {
        String sql = "UPDATE tasks SET finished=?,state=?,ended_at=?,successful=?,result=? WHERE task_id=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setBoolean(1, true);
            preparedStmt.setString(2, State.FINISHED.name());
            preparedStmt.setString(3, UtilityFunctions.timeNow());
            preparedStmt.setBoolean(4, is_successful);
            preparedStmt.setString(5, UtilityFunctions.convertObjectToGson(taskResult));
            preparedStmt.setString(6, tid);
            preparedStmt.executeUpdate();

        } catch (Exception e) {
            log.error("UPDATE FINISHED TASK ERROR " + e.getLocalizedMessage());
        }

    }

    public void listenToMessage() throws Exception {
        Queue queue = new Queue();
        queue.createExchangeQueue(QUEUE_NAME, this.EXCHANGE_NAME, "direct", ROUTING_KEY_NAME);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String queueItemStr = new String(delivery.getBody(), "UTF-8");
            QueueItem queueItem = new ObjectMapper().readValue(queueItemStr, QueueItem.class);
            try {
                doWork(queueItem, queue);
            } finally {
                log.info(" [x] Done");
                queue.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }

        };
        queue.listenToQueue(QUEUE_NAME, deliverCallback);

    }

    private void doWork(QueueItem queueItem, Queue queue) {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
        boolean is_successful = false;
        try {
            updateONGoingTaskStatusInDb(queueItem.getTask().getTid());

            switch (queueItem.getJob().getJobType()) {
                case BOOKING:
                    BookingHandler bookingHandler = new BookingHandler();
                    result = bookingHandler.createBooking(queueItem, dbConnection());
                    break;
                case SEARCH:
                    System.out.println("JOB TYPE ");
                    break;
                default:
                    throw new Exception("Unknown job type :" + queueItem.getJob().getJobType());

            }

        } catch (Exception e) {

            queue.createExchangeQueue(this.getConfigValue("LOST_QUEUE_NAME"), this.EXCHANGE_NAME, "direct",
                    this.getConfigValue("LOST_ROUTING_KEY"));
            String message = UtilityFunctions.convertObjectToJson(queueItem);
            queue.sendMessage(this.EXCHANGE_NAME, this.getConfigValue("LOST_ROUTING_KEY"), message);
            log.error(" [x] Error :" + e.getLocalizedMessage());

        } finally {
            if (Boolean.TRUE.equals(result.get("result").get("success"))) {
                is_successful = true;
            }

            updateFinishedTaskInDb(queueItem.getTask().getTid(), is_successful, result.get("result"));
        }

    }

}
