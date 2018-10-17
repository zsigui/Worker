package sg.jackiez.worker.app;

import sg.jackiez.worker.debug.TestVendorManager;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.utils.SLogUtil;

public class Main {

    public static void main(String[] args) {
        SLogUtil.v(new FutureRestApiV3().getLeverRate("eos"));
    }

}
