package org.example.java_web_service_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class JavaWebServiceProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaWebServiceProjectApplication.class, args);
    }

}
