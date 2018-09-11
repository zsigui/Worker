package sg.jackiez.worker.module.ok.model.base;

import sg.jackiez.worker.module.ok.utils.JsonUtil;

/**
 * 模型基础类，目前复写默认的toString
 */
public abstract class BaseM {

    @Override
    public String toString() {
        return JsonUtil.objToJson(this);
    }
}
