package com.tairanchina.csp.dew.example.idempotent;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.idempotent.DewIdempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/idempotent/")
public class IdempotentController {

    @GetMapping(value = "manual-confirm")
    public Resp<String> testManualConfirm(@RequestParam("str") String str) {
        try {
            Thread.sleep(1000);
            // 手工确认
            DewIdempotent.confirm();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Resp.success(str);
    }

    @GetMapping(value = "auto-confirm")
    public Resp<String> testAutoConfirm(@RequestParam("str") String str) {
        return Resp.success(str);
    }
}
