package cn.springcloud.eureka;

import cn.springcloud.eureka.utils.springExt.SpringUtil2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

@EnableEurekaClient
@SpringBootApplication
public class EurekaAdminServer {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(EurekaAdminServer.class, args);
        SpringUtil2.setApplicationContext(ctx);
    }
}
