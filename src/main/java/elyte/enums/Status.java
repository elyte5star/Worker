package elyte.enums;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Status implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private State state;

    private boolean finished;

    private boolean successful;
    
}
