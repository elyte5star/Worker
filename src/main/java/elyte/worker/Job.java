package elyte.worker;
import java.io.Serializable;
import elyte.enums.Status;
import elyte.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job implements Serializable{

    private static final long serialVersionUID = 1234567L;

    private String jid;
    private String created;
    private Set<Task> tasks;
    private String jobRequest;
    private JobType jobType;
    private Status jobStatus;
    private int numberOfTasks;

}
