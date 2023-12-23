package org.elyte.worker;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueueItem {

    @JsonProperty("Job")
    private Job job;

    @JsonProperty("Task")
    private Task task;
    
}
