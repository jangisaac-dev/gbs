package dev.oth.gbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = {"dev.oth.gbs"})
public class GamzaBackendStudyJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamzaBackendStudyJavaApplication.class, args);
    }

}
