package org.elyte.search;

import java.sql.Connection;
import org.elyte.queue.QueueItem;
import org.elyte.util.AppConfig;
import org.elyte.worker.WorkResult;
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
