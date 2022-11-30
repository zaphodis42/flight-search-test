package in.devkiranpatil.test.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.devkiranpatil.test.dto.City;
import in.devkiranpatil.test.service.impl.DataStoreImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FlightRouteConfig {
	@Autowired
	ObjectMapper mapper;
	@Autowired
	DataStoreImpl datastore;

	@PostConstruct
	public void buildGraph() {
		System.out.println("Building graph...");
		File file = null;
		try {
//			file = ResourceUtils.getFile("classpath:test-sched-small.csv");
			file = ResourceUtils.getFile("classpath:ivtest-sched.csv");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				System.out.println("===");

				String[] data = line.split(",");

				City depart = datastore.getOrBuildCity(data[1]);
				City arrive = datastore.getOrBuildCity(data[2]);

				datastore.addFlight(data[0], depart, arrive, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		}
		
		try {
			log.info(mapper.writeValueAsString(datastore.getOrBuildCity("AMD")));
			
			
//			datastore.listOfFlights(datastore.getOrBuildCity("AMD"), datastore.getOrBuildCity("CCU"));
			datastore.listOfFlights(datastore.getOrBuildCity("ATQ"), datastore.getOrBuildCity("BLR"), 5);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
