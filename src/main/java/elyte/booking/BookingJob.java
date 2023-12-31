package elyte.booking;
import java.math.BigDecimal;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingJob {
    private String userid;
    private BigDecimal totalPrice;
    private Cart cart;
    private ShippingAddress shippingAddress;
    
}
