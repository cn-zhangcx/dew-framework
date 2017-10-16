package com.tairanchina.csp.dew.example.jdbc;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工程启动类
 */
@ComponentScan(basePackageClasses = {Dew.class, JDBCExampleApplication.class})
public class JDBCExampleApplication extends DewBootApplication {

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(JDBCExampleApplication.class).run(args);
    }

}
