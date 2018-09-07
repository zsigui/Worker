package sg.jackiez.worker.module.ok.model.account;

import java.util.HashMap;

public class MultiFund {

    /**
     * 账户余额
     */
    public HashMap<String, String> free;
    /**
     * 账户冻结余额
     */
    public HashMap<String, String> freezed;

    @Override
    public String toString() {
        return "MultiFund{" +
                "free=" + free +
                ", freezed=" + freezed +
                '}';
    }
}
