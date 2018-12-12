package com.cisdi.info.simple;

import com.cisdi.info.simple.entity.permission.Module;
import com.cisdi.info.simple.util.ModuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.Collection;

/**
 *
 * @author CISDI
 * @date 2018/04/27
 */
@SpringBootApplication(scanBasePackages = {"com.cisdi.info.simple.*"}, exclude = {SecurityAutoConfiguration.class})
@EntityScan("com.cisdi.info.*")
@EnableDiscoveryClient(autoRegister = false)
@EnableRedisHttpSession
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

}