package in.devkiranpatil.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.devkiranpatil.test.dto.Flight;
import in.devkiranpatil.test.service.SearchService;

@Service
public class FlightScheduleSearchServiceImpl implements SearchService{

	@Autowired
	ObjectMapper mapper;
	@Autowired
	DataStoreImpl datastore;
	
	@Override
	public String findFlightsByLeastTime(String startCity, String endCity, int limit) {
		return datastore.listOfFlights(datastore.getOrBuildCity(startCity), datastore.getOrBuildCity(endCity), 5);
	}

	@Override
	public List<Flight> findFlightsFromCity(String startCity) {
		return datastore.getOrBuildCity(startCity).getFlights();
	}

}
