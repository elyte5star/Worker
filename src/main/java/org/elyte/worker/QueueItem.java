package org.elyte.worker;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueueItem implements Serializable{

    private static final long serialVersionUID = 1L;

    private Job job;

    private Task task;
    
}
