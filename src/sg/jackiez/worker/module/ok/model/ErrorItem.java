package sg.jackiez.worker.module.ok.model;

import sg.jackiez.worker.module.ok.model.base.BaseM;

public class ErrorItem extends BaseM {

    public int code;
    public String msg;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ErrorItem
                && ((ErrorItem)obj).code == code ) {
            return true;
        }
        return super.equals(obj);
    }
}
