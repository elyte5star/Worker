package org.elyte.booking;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;


import org.elyte.util.UtilityFunctions;

@Data
public class BookingHandler {

    public Map<String, Object> createBooking(Map<String, Map<String, Object>> queueJob, Connection conn)
            throws Exception {
        Map<String, Object> responseMessage = new HashMap<>();

        //System.out.println(" [x] Received '" + queueJob + "'");

        String sql = " insert into bookings (booking_id,owner_id, total_price, shipping_details,cart)"
                + " values (?, ?, ?, ?,?)";
        //String booking_id = UtilityFunctions.generateUuidString();
        
        //Map<String, Map<String, Object>> job = UtilityFunctions.jsonToMap(queueJob.get("job"));

       System.out.println("request " );

        try (PreparedStatement preparedStmt = conn.prepareStatement(sql)) {
           // System.out.println("To do " + queueJob);
             //preparedStmt.setString(1, UtilityFunctions.generateUuidString());
            // preparedStmt.setString(2, worker.getCreated());
            // preparedStmt.setString(3, worker.getWorkerType().toString());
            //preparedStmt.setObject(4, worker.getQueueName());
            // int rowsInserted = preparedStmt.executeUpdate();
            // System.out.print("NUMBER OF ROWS INSERTED: " + rowsInserted);
            // responseMessage.put("status", true);
            // responseMessage.put("message", rowsInserted);

        } catch (Exception e) {
            responseMessage.put("status", false);
            responseMessage.put("message", e.getMessage());
            System.err.println("ERROR" + e);
        }

        return responseMessage;

        // byte[] data = null;
    // //book = new Books();
    // try {
    //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //     ObjectOutputStream oos = new ObjectOutputStream(baos);
    //     oos.writeObject(book);
    //     oos.flush();
    //     oos.close();
    //     baos.close();
    //     data = baos.toByteArray();
    // }
    // catch(IOException ex) {
    //     JOptionPane.showMessageDialog(null, ex.getMessage());
    // }

    // try {

    //     //conn.setAutoCommit(false);
    //     state = conn.prepareStatement(query);
    //     state.setInt(1, isbn);
    //     state.setString(2, name);
    //     state.setObject(3, data);
    //     state.executeUpdate();
    //     //conn.commit();
    // }
    // catch(SQLException ex) {
    //     JOptionPane.showMessageDialog(null, ex.getMessage());
    // }
    // finally {
    //     close(3);
    // }

    }

}
