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
import org.elyte.enums.Status;
import org.elyte.enums.WorkerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.DeliverCallback;
import org.elyte.util.UtilityFunctions;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseWorker {

    private WorkerType workerType;
    private String QUEUE_NAME = "BOOKING";
    private String EXCHANGE_NAME = "elyteExchange";
    private String KEY_NAME = "rkey-two";

    // JDBC driver name and database URL

    static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static final String DB_URL = "jdbc:mariadb://localhost:3306/elyte";
   

    public CreateWorker createWorker() {
        CreateWorker worker = new CreateWorker(UUID.randomUUID().toString(), UtilityFunctions.timeNow(),
                this.workerType,
                this.QUEUE_NAME);
        return worker;
    }

    private final static Connection dbConnection() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (final ClassNotFoundException e) {
            System.err.println("ERROR" + e);
        }
        try {
            System.out.println("Connecting to a selected database...");
            final Connection con = DriverManager.getConnection(DB_URL, "userExample", "54321");
            return con;
        } catch (SQLException e) {
            System.err.println("ERROR " + e);
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
            System.out.print("NUMBER OF ROWS INSERTED: " + rowsInserted);
        } catch (Exception e) {
            System.err.println("ERROR" + e);
        } 
    }

    public void updateONGoingTaskStatusInDb(String tid, Status taskStatus) throws Exception {
        String sql = "UPDATE tasks SET status=?, started=? WHERE task_id=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setObject(1, entityToObject(taskStatus));
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, tid);
            int rowsInserted = preparedStmt.executeUpdate();
            System.out.println("NUMBER OF ROWS INSERTED: " + rowsInserted);
        } catch (Exception e) {
            System.err.println("UPDATE ERROR 1" + e.getLocalizedMessage());
        } 

    }

    public void updateFinishedTaskInDb(String tid, Status taskStatus, Map<String, Object> result) throws Exception {
        String sql = "UPDATE tasks SET status=?, finished=?WHERE task_id=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setString(1, taskStatus.toString());
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, result.toString());
            preparedStmt.setString(4, tid);
            int rowsInserted = preparedStmt.executeUpdate();
            System.out.println("NUMBER OF ROWS INSERTED: " + rowsInserted);
        } catch (Exception e) {
            System.err.println("UPDATE ERROR 2" + e.getLocalizedMessage());
        } 

    }

    public void listenToMessage() {
        Queue queue = new Queue();
        queue.createExchangeQueue(QUEUE_NAME, EXCHANGE_NAME, "direct", KEY_NAME);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String queueItemStr = new String(delivery.getBody(), "UTF-8");
            QueueItem queueItem = new ObjectMapper().readValue(queueItemStr, QueueItem.class);
            try {
                Map<String, Object> result = doWork(queueItem);

            } catch (Exception e) {

                System.err.println("ERROR" + e.getLocalizedMessage());

            } finally {
                System.err.println(" [x] Done");
                queue.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            }

        };
        queue.listenToQueue(QUEUE_NAME, deliverCallback);

    }

    private Map<String, Object> doWork(QueueItem queueItem) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Status jobStatus = new Status(State.PENDING, false);
            updateONGoingTaskStatusInDb(queueItem.getTask().getTid(), jobStatus);
            switch (queueItem.getJob().getJobType()) {
                case BOOKING:
                    BookingHandler bookingHandler = new BookingHandler();
                    result = bookingHandler.createBooking(queueItem, dbConnection());
                    break;
                case SEARCH:
                    System.out.println("JOB TYPE ");
                    break;
                default:
                    result = Map.of("status", false, "message", "Unknown Job type");
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public Object entityToObject(Status taskStatus) {

        byte[] data = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(taskStatus);
            oos.flush();
            oos.close();
            baos.close();
            data = baos.toByteArray();
        } catch (IOException ex) {
            data = null;
            System.err.println("ERROR :" + ex.getLocalizedMessage());

        }

        return data;

    }

    @Override
    public String toString() {
        return "BaseWorker{" +
                "workerType=" + workerType +
                ", QUEUE_NAME='" + QUEUE_NAME + '\'' +
                ", EXCHANGE_NAME='" + EXCHANGE_NAME + '\'' +
                ", KEY_NAME='" + KEY_NAME + '\'' +
                '}';
    }
}
