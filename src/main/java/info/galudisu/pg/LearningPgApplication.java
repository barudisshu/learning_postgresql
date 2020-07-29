package info.galudisu.pg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class LearningPgApplication {

  public static void main(String[] args) {
    SpringApplication.run(LearningPgApplication.class, args);
  }
}
