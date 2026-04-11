package free.servpp.cbodjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"free.servpp.cbodjava", "cbod.java"})
public class CbodjavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbodjavaApplication.class, args);
    }

}
