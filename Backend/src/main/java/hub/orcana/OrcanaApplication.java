package hub.orcana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrcanaApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrcanaApplication.class, args);
	}

}
