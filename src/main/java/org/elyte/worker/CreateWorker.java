package org.elyte.worker;

import org.elyte.enums.WorkerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorker {

    private String wid;
  
    private String created;

    private Enum<WorkerType> workerType;

    private String queueName;
    
}
