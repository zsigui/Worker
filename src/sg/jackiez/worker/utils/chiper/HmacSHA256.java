package sg.jackiez.worker.utils.chiper;

import java.io.UnsupportedEncodingException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sg.jackiez.worker.utils.Config;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/16
 */
public class HmacSHA256 {

	private static final String TAG = "HmacSHA256";
	private static final String HMAC_SHA256 = "HmacSHA256";

	public static byte[] genDigest(byte[] data, byte[] passphrase) {
		if (CommonUtil.isEmpty(data) || CommonUtil.isEmpty(passphrase)) {
			return null;
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(passphrase, HMAC_SHA256);
		try {
			Mac mMac = Mac.getInstance(HMAC_SHA256);
			mMac.init(secretKeySpec);
			mMac.update(data);
			return mMac.doFinal();
		} catch (Exception e) {
			SLogUtil.e(TAG, e);
		}
		return null;
	}

	public static byte[] genDigest(String data, String passphrase) {
		if (CommonUtil.isEmpty(data) || CommonUtil.isEmpty(passphrase)) {
			return null;
		}
		try {
			return genDigest(data.getBytes(Config.DEFAULT_SYS_CHARSET),
					passphrase.getBytes(Config.DEFAULT_SYS_CHARSET));
		} catch (UnsupportedEncodingException e) {
			SLogUtil.e(TAG, e);
		}
		return null;
	}
}
