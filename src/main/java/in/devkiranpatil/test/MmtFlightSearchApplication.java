package in.devkiranpatil.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class MmtFlightSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmtFlightSearchApplication.class, args);
	}

	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}
}
