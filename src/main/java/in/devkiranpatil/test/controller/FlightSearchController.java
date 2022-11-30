package in.devkiranpatil.test.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.devkiranpatil.test.dto.Flight;
import in.devkiranpatil.test.service.SearchService;


@RestController
public class FlightSearchController {
	@Autowired
	private SearchService searchService;

	@GetMapping("/api/flight/searchByLeastTime")
	public String searchByTime(@RequestParam("startCity") String startCity,
			@RequestParam("destCity") String destCity) {

		return searchService.findFlightsByLeastTime(startCity, destCity, 5);

	}

	@GetMapping("/api/flight/search")
	public List<Flight> searchByParam(@RequestParam("startCity") String startCity) {

		return searchService.findFlightsFromCity(startCity);

	}
}
