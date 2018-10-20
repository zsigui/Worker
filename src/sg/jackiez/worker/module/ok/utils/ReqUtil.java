package sg.jackiez.worker.module.ok.utils;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/20
 */
public class ReqUtil {


	public static <T> T retry(int retryTime, Action<T> action) {
		int tryTime = 0;
		T resp = null;
		while (resp == null && tryTime++ < retryTime) {
			resp = action.handle();
		}
		return resp;
	}

	public interface Action<T> {
		T handle();
	}
}
