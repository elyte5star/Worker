package org.elyte.worker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import org.elyte.enums.JobStatus;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Serializable{
    private String tid;

    private String created;

    private String jib;

    private String result;

    private String started;

   
    private String finished;

 
    private JobStatus status;

    
}
