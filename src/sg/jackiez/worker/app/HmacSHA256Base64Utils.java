// 使用时：请在支持java运行机器，创建java文件，修改package名，执行运行就好！
package sg.jackiez.worker.app;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import sg.jackiez.worker.module.ok.OkConfig;
import sg.jackiez.worker.utils.common.CommonUtil;

/**
 * Hmac SHA256 Base64 Signature Utils.<br/>
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/2/1 11:41
 */
public class HmacSHA256Base64Utils {


    public static void main(String[] args) {
        String timestamp = "2018-10-16T23:47:26.523Z";
        String method = "GET";
        String requestPath = "/api/futures/v3/accounts/eos/leverage";
        String queryString = null;
        String body = null;
        String secretKey = OkConfig.V3_SECRET_KEY;
        try {
            String sign = sign(timestamp, method, requestPath, queryString, body, secretKey);
            System.out.println("Hmac SHA256 Base64 Signature Result: " + sign);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private static final String EMPTY = "";
    private static final String QUESTION = "?";
    private static final String UTF_8 = "UTF-8";
    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Signing a Message.<br/>
     * <p>
     * using: Hmac SHA256 + base64
     *
     * @param timestamp   the number of seconds since Unix Epoch in UTC. Decimal values are allowed.
     *                    eg: 2018-03-08T10:59:25.789Z
     * @param method      eg: POST
     * @param requestPath eg: /orders
     * @param queryString eg: before=2&limit=30
     * @param body        json string, eg: {"product_id":"BTC-USD-0309","order_id":"377454671037440"}
     * @param secretKey   user's secret key eg: E65791902180E9EF4510DB6A77F6EBAE
     * @return signed string   eg: TO6uwdqz+31SIPkd4I+9NiZGmVH74dXi+Fd5X0EzzSQ=
     * @throws CloneNotSupportedException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public static String sign(String timestamp, String method, String requestPath,
                              String queryString, String body, String secretKey)
            throws CloneNotSupportedException, InvalidKeyException, UnsupportedEncodingException {
        if (CommonUtil.isEmpty(secretKey) || CommonUtil.isEmpty(method)) {
            return EMPTY;
        }
        String preHash = preHash(timestamp, method, requestPath, queryString, body);
        System.out.println("Hmac SHA256 Base64 Signature preHash: " + preHash);
        byte[] secretKeyBytes = secretKey.getBytes(UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, HMAC_SHA256);
        Mac mac = (Mac) MAC.clone();
        mac.init(secretKeySpec);
        return Base64.getEncoder().encodeToString(mac.doFinal(preHash.getBytes(UTF_8)));
    }

    /**
     * the prehash string = timestamp + method + requestPath + body .<br/>
     *
     * @param timestamp   the number of seconds since Unix Epoch in UTC. Decimal values are allowed.
     *                    eg: 2018-03-08T10:59:25.789Z
     * @param method      eg: POST
     * @param requestPath eg: /orders
     * @param queryString eg: before=2&limit=30
     * @param body        json string, eg: {"product_id":"BTC-USD-0309","order_id":"377454671037440"}
     * @return prehash string eg: 2018-03-08T10:59:25.789ZPOST/orders?before=2&limit=30{"product_id":"BTC-USD-0309",
     * "order_id":"377454671037440"}
     */
    public static String preHash(String timestamp, String method, String requestPath, String queryString, String body) {
        StringBuilder preHash = new StringBuilder();
        preHash.append(timestamp);
        preHash.append(method.toUpperCase());
        preHash.append(requestPath);
        if (!CommonUtil.isEmpty(queryString)) {
            preHash.append(QUESTION).append(queryString);
        }
        if (!CommonUtil.isEmpty(body)) {
            preHash.append(body);
        }
        return preHash.toString();
    }

    public static Mac MAC;

    static {
        try {
            MAC = Mac.getInstance(HMAC_SHA256);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Can't get Mac's instance.");
            e.printStackTrace();
            throw new RuntimeErrorException(new Error("Can't get Mac's instance."));
        }
    }

}
