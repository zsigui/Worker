package sg.jackiez.worker.app;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import sg.jackiez.worker.debug.TestVendorManager;
import sg.jackiez.worker.module.ok.network.future.FutureRestApiV3;
import sg.jackiez.worker.module.ok.utils.JsonUtil;
import sg.jackiez.worker.utils.DateUtil;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;

public class Main {

    public static void main(String[] args) {
        FutureRestApiV3 futureRestApiV3 = new FutureRestApiV3();
        List<KlineInfo> klineInfoList = JsonUtil.jsonToKlineList(futureRestApiV3.getKlineInfo("EOS-USD-181228",
                DateUtil.formatISOTime(System.currentTimeMillis() - 78 * 60 * 60 * 1000), DateUtil.formatISOTime(System.currentTimeMillis() - 54 * 60 * 60 * 1000), "60"));
        SLogUtil.v(klineInfoList);
        if(klineInfoList != null) {
            SLogUtil.v(klineInfoList.size());
        }
    }

}
