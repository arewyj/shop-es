package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/16
 * @Version V1.0
 **/
public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public final static String app_id = "2021000117622337";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public final static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCSIaKI3EKEnXDkfJVbsH3ucoYMerP0jMw43GPPSBzfNapVPmCENYu+Vd0t8Lz8oSpIfwfSdbh/a11IKTD8ci5iH1iU31ZaOGE2mJ18d8GeA3a2uiAR6AYL2zDy7CX5o6OKyaGntMRNm/t1bxgqevVSTmAd6aqviKrqgG0hejVev9w0Hb5jjVGeX7NkadlTjiJsvAqNde5DJ1wAB6W0z4b7xig+jemoUmEDnLGkZ4HxC1v7xNPUNAOurzHeAwJB6Uyho6rpGOHFMabiYumoh8/6TnPMDb++G+/9TpbpXuFqSL81XD44aeid8egmTgnFwy4YzIo+S4uDn1kAFlmBvpoFAgMBAAECggEAURSehUeJFOHygKRrjttHlGUw4X23maoBqk8Ghj1h36t8Hh4lGVZIekxUE6hug9G8j0sEgQ7WVsSwllhcmMWV3NW9paSh2h9MOSnGwAgGgvEzDkIJh0XxDOUKrM7AZ1oXZOtpaY8QGGvCwy7n/rIIqilYhWBgV1MCWmj1pEyVjkMTKgK3Hqvi2mJtoA2lT86cQbblBeinRDjAgadPYIZ6+wwe9qCj0QKJSX4AGjCjLFF+ywVdjdm7ZpbQZNRAu7mJrDEAjztUsZRvgGmuzxVzsjNIGExqs6mlIl0XsTflgK6qCLlISnK3lspDNRbv/YTc8+b7Ckkw1lg4rX2o5Zm3JQKBgQD2bEP8jU8jqnHAp9fDNtQ3Yye/079Mzh03vLwWVLcb5npf4kPP9nnQp4ujnleaNKdA5YA55ZOTfAhoBlIi7K72q2IvjkeOmy+MXyLVOZ5B7MdJJvhoxz+LGNI2CPI/0DgpA5N3Rn60tDoeAvOvRvK/GRc5gWbjot8WdtgQntbMpwKBgQCXz4oOFfLshu6sWmTr4eKsRb47VH/0s1LN/1TC45zo6/Lb2W4xJAqTKVHDbqbrm7hceNXrYoTqqmNvipjVXUBv0K9YQEUoEncTG4iu3Ci8CMpHKteTorGc2roFdyucUx7LsIvoep+ye9gBVlRXNiIL4fM70d002kYQeAG3PthdcwKBgQDcbfxi7gTc4preu0VGWsV6xjfIYKG/EqEQOsQFNcKWMpVFMCF45gIRo85Fjl7OkksKwkDxiWt5gnoCk6TlmNLp1GUAJUXdSvQ9nNyL3/6w+h1KUpHjKFivFIT10QriCmAyUACZsXWmL5HNRta088IxL0CXMCnZdtgYQDcGpw9+AQKBgBnhmxK9sTWb53Lg0lXYo9Fk7oKk7E+mhtZx0lyLe4PGPhg3IG8mSts97x+XbMO8P5kcTLQdjrWHKP6qLVYq2MJ3XH16L5AeXciXKB1PkW5FPV8WsbwMv6UTMSWfZIL5NMXbKm8PAMzoCjJoKmbX5sQJ2HL5W3IYSFukaf54q2B7AoGBAOXAsDMRsFo5RaUP+d00Cd/+n4UdFbN3jvMuPGXtSR8PDJHVgqj8WUXeg2aO5e2tcJNux0Ka39bPnThkWydYtb0chz/n6HXoU00GFExE0CBH5atiNnLaQ5UyVT52AdGjHFGeTerLkngEJBtjG3lKmM6lt6TCeHmdtjzmZQFMUvtm";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public final static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqyi3+g6uGFqMFPZkWn4ENam+d3IlpgxSNxNlIImsyiqGIt/0Fnag6Rb9i6XFruWmai/OTyXmNCZNHXqt0E41C2Rr55PtvrIYsl4ynAB2171ROZm75H2bjVbdAhLljjwiFhTJlLocNdM+ZitmsW3Rc9d/8patwIrvhADextKsTjgyYkxWGoQTakYYW5Kk8zh3gx4EH6e00F8BzSrE3X4fSUViZajSRrXbM/uXQJge6BnD4VCq4J7hEQe6YaYXjPz9Huqpw/gbp5JipzGJtn2e3MzwcsK+Z7kALryuvbNQR1VqMoqrDMJnTFxxDeME5JCGgc2HurTCTrFPqmk1J1C9hwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public final static String notify_url = "http://localhost:8900/pay/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public final static String return_url = "http://localhost:8900/pay/returnUrl";

    // 签名方式
    public final static String sign_type = "RSA2";

    // 字符编码格式
    public final static String charset = "utf-8";

    // 支付宝网关
    public final static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public final static String log_path = "D:\\";


    //↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
