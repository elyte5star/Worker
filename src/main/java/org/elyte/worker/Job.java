package org.elyte.worker;
import java.io.Serializable;
import java.util.List;
import org.elyte.enums.JobState;
import org.elyte.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job implements Serializable{
    private String jid;
    private String created;
    private List<Task> tasks;
    private String jobRequest;
    private Enum<JobState> jobType;
    private JobStatus jobStatus;
    private int numberOfTasks;
}
