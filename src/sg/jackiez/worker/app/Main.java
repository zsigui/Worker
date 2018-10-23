package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV1;

public class Main {

    public static void main(String[] args) {
        FutureDataGrabber grabber = new FutureDataGrabber("EOS-USD",
                OKTypeConfig.CONTRACT_TYPE_QUARTER,
                new FutureRestApiV1()
        );
        grabber.startAll();
        DBManager.get().startGrab();

    }

}
