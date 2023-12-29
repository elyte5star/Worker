package org.elyte;

import org.elyte.enums.WorkerType;
import org.elyte.util.AppConfig;
import org.elyte.worker.BaseWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWorker {

    private static final Logger log = LoggerFactory.getLogger(MainWorker.class);

    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        String start = config.timeNow();
        Runnable bookingWorker = new BaseWorker(WorkerType.BOOKING, config.getConfigValue("BOOKING_QUEUE_NAME"),
                config.getConfigValue("BOOKING_ROUTING_KEY"));
        Thread t1 = new Thread(bookingWorker);
        t1.start();
        Runnable searchWorker = new BaseWorker(WorkerType.SEARCH, config.getConfigValue("SEARCH_QUEUE_NAME"),
                config.getConfigValue("SEARCH_ROUTING_KEY"));
        Thread t2 = new Thread(searchWorker);
        t2.start();
        String end = config.timeNow();
        log.info("Started Threads with Ids: " + t1.getId() + " " + t2.getId() + " " + " in " + config.diff(start, end)
                + " milliseconds!" + " Date " +  config.timeNow());

    }

}