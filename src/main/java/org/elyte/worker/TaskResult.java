package org.elyte.worker;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResult implements Serializable{

   
    private String resid;

    
    private String resultDate;

    
    private Task task;

    
    private String data;

}
