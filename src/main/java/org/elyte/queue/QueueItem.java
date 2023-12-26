package org.elyte.queue;
import lombok.NoArgsConstructor;

import org.elyte.worker.Job;
import org.elyte.worker.Task;

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
