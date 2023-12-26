package org.elyte.booking;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.elyte.queue.QueueItem;
import org.elyte.util.UtilityFunctions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class BookingHandler {

    private static final Logger log = LoggerFactory.getLogger(BookingHandler.class);

    public Map<String, Map<String, Object>> createBooking(QueueItem queueItem, Connection conn)
            throws Exception {
        Map<String, Map<String, Object>> responseMessage = new HashMap<>();

        BookingJob bookingJob = new ObjectMapper().readValue(queueItem.getJob().getBookingRequest(), BookingJob.class);
        String sql = " insert into bookings (booking_id,created,owner_id, total_price, shipping_details,cart)"
                + " values (?, ?,?, ?, ?,?)";
        final String id = UtilityFunctions.generateUuidString();
        try (PreparedStatement preparedStmt = conn.prepareStatement(sql)) {
            preparedStmt.setString(1, id);
            preparedStmt.setString(2, UtilityFunctions.timeNow());
            preparedStmt.setString(3, bookingJob.getUserid());
            preparedStmt.setBigDecimal(4, bookingJob.getTotalPrice());
            preparedStmt.setObject(5, UtilityFunctions.convertObjectToGson(bookingJob.getShippingAddress()));
            preparedStmt.setObject(6, UtilityFunctions.convertObjectToGson(bookingJob.getCart()));
            preparedStmt.executeUpdate();

            responseMessage.put("result", Map.of("tid", queueItem.getTask().getTid(), "oid", id, "success", true));

        } catch (Exception e) {
            responseMessage.put("result", Map.of("tid", queueItem.getTask().getTid(), "oid", null, "success", false));
            log.error("ERROR " + e);
        }

        return responseMessage;

    }

}
