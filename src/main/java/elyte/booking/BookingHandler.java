package elyte.booking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import elyte.queue.QueueItem;
import elyte.util.AppConfig;
import elyte.worker.WorkResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
@Getter
@AllArgsConstructor
public class BookingHandler extends AppConfig{

    private static final Logger log = LoggerFactory.getLogger(BookingHandler.class);

    public WorkResult createBooking(QueueItem queueItem, Connection conn)
            throws Exception {
        BookingJob bookingJob = this.mapper.readValue(queueItem.getJob().getJobRequest(), BookingJob.class);
        String sql = " insert into bookings (booking_id,created,owner_id, total_price, shipping_details,cart)"
                + " values (?, ?,?, ?, ?,?)";
        final String Id = this.generateUuidString();
        try (PreparedStatement preparedStmt = conn.prepareStatement(sql)) {
            preparedStmt.setString(1, Id);
            preparedStmt.setString(2, this.timeNow());
            preparedStmt.setString(3, bookingJob.getUserid());
            preparedStmt.setBigDecimal(4, bookingJob.getTotalPrice());
            preparedStmt.setObject(5, this.convertObjectToJson(bookingJob.getShippingAddress()));
            preparedStmt.setObject(6, this.convertObjectToJson(bookingJob.getCart()));
            preparedStmt.executeUpdate();
            log.info("[+] CREATED BOOKING WITH ID: " + Id);
            return new WorkResult(queueItem.getTask().getTid(),true,Id);

        } 
       

    }

}
