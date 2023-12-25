package org.elyte.booking;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.elyte.util.UtilityFunctions;
import org.elyte.worker.QueueItem;

import com.fasterxml.jackson.databind.ObjectMapper;

@Data
public class BookingHandler {

    public Map<String, Object> createBooking(QueueItem queueItem, Connection conn)
            throws Exception {
        Map<String, Object> responseMessage = new HashMap<>();

        BookingJob bookingJob = new ObjectMapper().readValue(queueItem.getJob().getBookingRequest(), BookingJob.class);
        String sql = " insert into bookings (booking_id,created,owner_id, total_price, shipping_details,cart)"
                + " values (?, ?,?, ?, ?,?)";
        try (PreparedStatement preparedStmt = conn.prepareStatement(sql)) {
            preparedStmt.setString(1, UtilityFunctions.generateUuidString());
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, bookingJob.getUserid());
            preparedStmt.setBigDecimal(4, bookingJob.getTotalPrice());
            preparedStmt.setObject(5, UtilityFunctions.convertObjectToGson(bookingJob.getShippingAddress()));
            preparedStmt.setObject(6, UtilityFunctions.convertObjectToGson(bookingJob.getCart()));
            preparedStmt.executeUpdate();
            responseMessage.put("success", true);
            responseMessage.put("message", queueItem.getTask().getTid());

        } catch (Exception e) {
            responseMessage.put("success", false);
            responseMessage.put("message",queueItem.getTask().getTid());
            System.err.println("ERROR" + e);
        }

        return responseMessage;

        
    }

}
