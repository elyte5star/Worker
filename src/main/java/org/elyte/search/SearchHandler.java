package org.elyte.search;

import java.sql.Connection;
import org.elyte.queue.QueueItem;
import org.elyte.util.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;


@Setter
@Getter
@AllArgsConstructor
public class SearchHandler extends AppConfig {
    private static final Logger log = LoggerFactory.getLogger(SearchHandler.class);

    public Map<String, Object> search(QueueItem queueItem, Connection conn)  throws Exception{
        log.info("To be implemented");
        Map<String,Object> result= new HashMap<String,Object>();
        result.put("taskId", queueItem.getTask().getTid());
        result.put( "data", null);
        result.put( "success", false);
        return result;

    }

}
