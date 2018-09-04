package sg.jackiez.worker.module.ok;

import java.util.HashMap;
import java.util.List;

import sg.jackiez.worker.module.ok.model.ErrorItem;
import sg.jackiez.worker.utils.FileUtil;
import sg.jackiez.worker.utils.ModelUtil;

/**
 * 进行OK交易的特殊处理帮助类
 */
public class OKHelper {

    private HashMap<Integer, ErrorItem> mSpotErrMap = null;

    public OKHelper() {
        init();
    }

    private void init() {
        if (mSpotErrMap == null) {
            List<ErrorItem> items = ModelUtil.readModelFromCustomFile(FileUtil.getFileBaseCurrentWork(OkConfig.FILE_SPOT_ERROR),
                    ErrorItem.class);
            if (items != null) {
                mSpotErrMap = new HashMap<>(items.size());
                for (ErrorItem item : items) {
                    mSpotErrMap.put(item.code, item);
                }
            }
        }
    }

    public ErrorItem findErrorItem(int code) {
        if (mSpotErrMap == null) {
            return null;
        }
        return mSpotErrMap.get(code);
    }
}
