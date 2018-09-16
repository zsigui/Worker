package sg.jackiez.worker.callback;

/**
 * @Author JackieZ
 * @Date Created on 2018/9/16
 */
public interface SimpleCallback<T> {

	void onSuccess(T data);

	void onFail(int code, String msg);
}
