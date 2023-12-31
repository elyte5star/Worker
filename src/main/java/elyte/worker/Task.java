package elyte.worker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import elyte.enums.Status;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Serializable{
    private static final long serialVersionUID = 1234567L;
    private String tid;
    private String created;
    private String startedAt;
    private String endedAt;
    private Status taskStatus;
    private String result;

}
