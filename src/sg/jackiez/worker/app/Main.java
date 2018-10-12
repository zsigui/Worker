package sg.jackiez.worker.app;

import sg.jackiez.worker.debug.TestVendorManager;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        SLogUtil.setPrintFile(true);

        new TestVendorManager().start();
    }

}
