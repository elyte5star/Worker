package org.elyte.enums;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private JobState jobState;

    private boolean finished=false;
    
}
