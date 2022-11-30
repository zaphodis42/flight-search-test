package in.devkiranpatil.test.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import in.devkiranpatil.test.dto.Flight;

public interface SearchService {
	String findFlightsByLeastTime(String startCity, String endCity, int limit);

	List<Flight> findFlightsFromCity(String startCity);
}
