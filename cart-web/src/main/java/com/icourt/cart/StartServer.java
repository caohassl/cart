package com.icourt.cart;

import com.icourt.core.log.EnableJsonLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author Caomr
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class},scanBasePackages="com.icourt")
@EnableAutoConfiguration
@EnableJsonLogger
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients
@Slf4j
public class StartServer {

    public static void main(String[] args) {
        SpringApplication.run(StartServer.class, args);
        log.info("-------启动服务成功！-------");
    }
}
