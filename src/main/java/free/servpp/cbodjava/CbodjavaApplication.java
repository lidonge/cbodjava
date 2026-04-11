package free.servpp.cbodjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"free.servpp.cbodjava", "cbod.java", "free.cobol2java.java"})
@MapperScan("free.servpp.cbodjava.cics_vsam")
public class CbodjavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbodjavaApplication.class, args);
    }

}
