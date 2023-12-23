package org.elyte;

import org.elyte.worker.BaseWorker;

public class MainWorker {

    public static void main(String[] args) throws Exception {
        
        BaseWorker baseWorker = new BaseWorker();
        baseWorker.listenToMessage();

    }

}