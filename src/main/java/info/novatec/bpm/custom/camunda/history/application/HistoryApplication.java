package info.novatec.bpm.custom.camunda.history.application;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableProcessApplication("custom-history-implementation")
@ComponentScan
public class HistoryApplication {

    public static void main(String... args) {
        SpringApplication.run(HistoryApplication.class, args);
    }
}