package elyte.worker;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkResult {

    private String tid;

    private boolean success;

    private String result;
    
}
