package elyte.search;

import java.sql.Connection;
import elyte.queue.QueueItem;
import elyte.util.AppConfig;
import elyte.worker.WorkResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SearchHandler extends AppConfig {
    private static final Logger log = LoggerFactory.getLogger(SearchHandler.class);
    public WorkResult search(QueueItem queueItem, Connection conn) throws Exception {
        log.warn("Not implemented");
        return new WorkResult(queueItem.getTask().getTid(), true, null);

    }

}
