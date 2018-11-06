package sg.jackiez.worker.app;

import sg.jackiez.worker.module.ok.OKTypeConfig;
import sg.jackiez.worker.module.ok.handler.FutureDataGrabber;
import sg.jackiez.worker.module.ok.manager.DBManager;
import sg.jackiez.worker.module.ok.manager.PrecursorManager;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        SLogUtil.setPrintFile(true);
        SLogUtil.setDebugLevel(SLogUtil.Level.INFO);
        PrecursorManager.get().init(OKTypeConfig.SYMBOL_EOS, OKTypeConfig.CONTRACT_TYPE_QUARTER);
        FutureDataGrabber grabber = new FutureDataGrabber(PrecursorManager.get().getInstrumentId());
        grabber.startAll();
        DBManager.get().startGrab();
    }

}
