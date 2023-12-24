package org.elyte.worker;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.elyte.booking.BookingHandler;
import org.elyte.enums.JobStatus;
import org.elyte.enums.JobType;
import org.elyte.enums.WorkerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.DeliverCallback;
import org.elyte.util.UtilityFunctions;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private static Connection conn = null;

    public CreateWorker createWorker() {
        CreateWorker worker = new CreateWorker(UUID.randomUUID().toString(), UtilityFunctions.timeNow(), this.workerType,
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
            System.err.println("ERROR" + e);
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
        } finally {
            conn.close();

        }

    }

    public void updateONGoingTaskStatusInDb(String tid, JobStatus taskStatus) throws Exception {
        String sql = "UPDATE TASKS SET STATUS=?, STARTED=? WHERE TASK_ID=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setString(1, taskStatus.toString());
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, tid);
            int rowsInserted = preparedStmt.executeUpdate();
            System.out.println("NUMBER OF ROWS INSERTED: " + rowsInserted);
        } catch (Exception e) {
            System.err.println("ERROR" + e);
        } finally {
            conn.close();

        }

    }

    public void updateFinishedTaskInDb(String tid, JobStatus taskStatus, Map<String, Object> result) throws Exception {
        String sql = "UPDATE TASKS SET STATUS=?, FINISHED=?,RESULT=? WHERE TASK_ID=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setString(1, taskStatus.toString());
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, result.toString());
            preparedStmt.setString(4, tid);
            int rowsInserted = preparedStmt.executeUpdate();
            System.out.println("NUMBER OF ROWS INSERTED: " + rowsInserted);
        } catch (Exception e) {
            System.err.println("ERROR" + e);
        } finally {
            conn.close();

        }

    }

    public void listenToMessage() {
        Queue queue = new Queue();
        queue.createExchangeQueue(QUEUE_NAME, EXCHANGE_NAME, "direct", KEY_NAME);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String queueItemStr = new String(delivery.getBody(), "UTF-8");
            QueueItem queueItem = new ObjectMapper().readValue(queueItemStr,QueueItem.class);
            try {
                Map<String, Object> result =doWork(queueItem);

            } catch (Exception e) {

                e.printStackTrace();

            } finally {
                System.err.println(" [x] Done");
                queue.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            }

        };
        queue.listenToQueue(QUEUE_NAME, deliverCallback);

    }

    private Map<String, Object> doWork(QueueItem queueItem) throws Exception {
        Job job = queueItem.getJob();
        Map<String, Object> result = new HashMap<String, Object>();
        switch (job.getJobType()) {
            case BOOKING:
                BookingHandler bookingHandler = new BookingHandler();
                result = bookingHandler.createBooking(queueItem, dbConnection());
                break;
            case SEARCH:
                System.out.println("JOB TYPE1 ");
                break;
            default:
                result = Map.of("status", false, "message", "Unknown Job type");
                break;
        }
        return result;

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
