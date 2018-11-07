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

	public static Boolean blockJudge(int elapseTime, int maxWaitTime, Action<Boolean> action) {
		long startTime = System.currentTimeMillis();
		boolean has = false;
		while (!has && (maxWaitTime == -1 || System.currentTimeMillis() - startTime < maxWaitTime)) {
			has = action.handle();
			try {
				Thread.sleep(elapseTime);
			} catch (InterruptedException ignored) {
			}
		}
		return has;
	}

	public interface Action<T> {
		T handle();
	}
}
