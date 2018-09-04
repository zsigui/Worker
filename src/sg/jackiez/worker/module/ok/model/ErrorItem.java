package sg.jackiez.worker.module.ok.model;

public class ErrorItem {

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

    @Override
    public String toString() {
        return "{\"code\":" + code + ", \"msg\"=\"" + msg + "\"}";
    }
}
