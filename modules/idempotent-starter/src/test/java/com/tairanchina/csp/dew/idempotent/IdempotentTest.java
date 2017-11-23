package com.tairanchina.csp.dew.idempotent;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IdempotentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IdempotentTest {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentTest.class);

    private String urlPre = "http://localhost:8080/idempotent/";

    @Autowired
    private DewIdempotentConfig dewIdempotentConfig;

    @Test
    public void testManualConfirm() throws IOException, InterruptedException {
        HashMap<String, String> hashMap = new HashMap<String, String>() {{
            put(dewIdempotentConfig.getOptTypeFlag(), "manualConfirm");
            put(dewIdempotentConfig.getOptIdFlag(), "0001");
            put(dewIdempotentConfig.getOptExpireMsFlag(), "5000");
        }};
        // 第一次请求，正常
        Resp<String> result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
       Thread thread = new Thread(() -> {
            // 上一次请求还在进行中
            try {
                Resp<String> result2 = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test", hashMap), String.class);
                Assert.assertEquals(StandardCode.CONFLICT.toString(), result2.getCode());
                Thread.sleep(1000);
                // 上一次请求已确认，不能重复请求
                result2 = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test", hashMap), String.class);
                Assert.assertEquals(StandardCode.LOCKED.toString(), result2.getCode());
                Thread.sleep(4000);
                // 幂等过期，可以再次提交
                result2 = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test", hashMap), String.class);
                Assert.assertTrue(result2.ok());
            } catch (Exception ignore) {

            }
        });
        thread.start();
        thread.join();
    }

    @Test
    public void testAutoConfirmed() throws IOException, InterruptedException {
        HashMap<String, String> hashMap = new HashMap<String, String>() {{
            put(dewIdempotentConfig.getOptTypeFlag(), "autoConfirm");
            put(dewIdempotentConfig.getOptIdFlag(), "0001");
            put(dewIdempotentConfig.getOptExpireMsFlag(), "5000");
            put(dewIdempotentConfig.getOptNeedConfirmFlag(), "false");
        }};
        // 第一次请求，正常
        Resp<String> result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
        // 上一次请求已确认，不能重复请求
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        Thread.sleep(5000);
        // 幂等过期，可以再次提交
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
    }

    @Test
    public void testIgnore() throws IOException, InterruptedException {
        HashMap<String, String> hashMap = new HashMap<String, String>() {{
            put(dewIdempotentConfig.getOptTypeFlag(), "ignore");
            put(dewIdempotentConfig.getOptIdFlag(), "0001");
            put(dewIdempotentConfig.getOptNeedConfirmFlag(), "false");
        }};
        // 第一次请求，正常
        Resp<String> result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
        // 上一次请求已确认，不能重复请求
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        // 忽略幂等检查
        hashMap.put("__IDEMPOTENT_IGNORE__", "true");
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
    }

}
