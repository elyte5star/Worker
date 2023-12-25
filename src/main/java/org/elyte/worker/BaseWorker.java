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

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/elyte?" + "user=userExample&password=54321";

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
            System.err.println("ERROR " + e);
        }
        try {
            System.out.println("Connecting to a selected database...");
            final Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            System.err.println("SQLState: " + ex.getSQLState());
            System.err.println("VendorError: " + ex.getErrorCode());
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

    public void updateFinishedTaskInDb(String tid, boolean is_successful) throws Exception {
        String sql = "UPDATE tasks SET finished=?,state=?,ended_at=?,successful=? WHERE task_id=?";
        try (PreparedStatement preparedStmt = dbConnection().prepareStatement(sql)) {
            preparedStmt.setBoolean(1, true);
            preparedStmt.setString(2, State.FINISHED.name());
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setBoolean(3, is_successful);
            preparedStmt.setString(4, tid);
            preparedStmt.executeUpdate();
           
        }

    }

    public void listenToMessage() throws Exception{
        Queue queue = new Queue();
        queue.createExchangeQueue(QUEUE_NAME, EXCHANGE_NAME, "direct", KEY_NAME);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String queueItemStr = new String(delivery.getBody(), "UTF-8");
            QueueItem queueItem = new ObjectMapper().readValue(queueItemStr, QueueItem.class);
            try {
                doWork(queueItem);

            } finally {
                System.out.println(" [x] Done");
                queue.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }

        };
        queue.listenToQueue(QUEUE_NAME, deliverCallback);

    }

    private void doWork(QueueItem queueItem) {
        Map<String, Object> result = new HashMap<String, Object>();
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
                // result = Map.of("status", false, "message", "Unknown Job type");
                // break;
            }

        } catch (Exception e) {

            System.err.println(" [x] Error :" + e.getLocalizedMessage());

        }

        System.out.println(result.get("status") + " " + result.get("message"));


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
