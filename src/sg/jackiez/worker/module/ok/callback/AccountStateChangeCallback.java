package sg.jackiez.worker.module.ok.callback;

/**
 * 回调账号信息状态
 *
 * @Author JackieZ
 * @Date Created on 2018/10/1
 */
public interface AccountStateChangeCallback {

	/**
	 * 当账号信息更新时进行通知
	 */
	void onAccountInfoUpdated();

	/**
	 * 当账号信息超时需要更新时进行通知
	 */
	void onAccountInfoOutdated();
}
