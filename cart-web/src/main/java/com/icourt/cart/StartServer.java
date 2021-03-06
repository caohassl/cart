package com.icourt.cart;

import com.icourt.core.log.EnableJsonLogger;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
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
@SpringBootApplication(scanBasePackages="com.icourt")
@EnableAutoConfiguration
@EnableJsonLogger
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients
@MapperScan("com.icourt.cart.dao")
@Slf4j
public class StartServer {

    public static void main(String[] args) {
        SpringApplication.run(StartServer.class, args);
        log.info("-------启动服务成功！-------");
    }
}
