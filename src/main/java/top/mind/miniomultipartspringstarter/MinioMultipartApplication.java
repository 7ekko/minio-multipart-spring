package top.mind.miniomultipartspringstarter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("top.mind.**.mapper")
@SpringBootApplication
public class MinioMultipartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinioMultipartApplication.class, args);
    }
}
