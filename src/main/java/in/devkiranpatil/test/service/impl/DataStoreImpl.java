package in.devkiranpatil.test.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.devkiranpatil.test.dto.City;
import in.devkiranpatil.test.dto.Flight;
import in.devkiranpatil.test.dto.FlightRoute;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataStoreImpl {

	@Autowired
	ObjectMapper mapper;

	public final static int MINUTES_IN_A_DAY = 24 * 60;
	public final static int SHORTEST_TIME_TO_CHANGE_PLANES = 120;

	// Data fields
	private Map<String, City> cityMap;
	private int size;

	@PostConstruct
	private void init() {
		cityMap = new HashMap<>();
	}

	public String listOfFlights(City startCity, City destCity, int limit) {

		String result = "";
		log.info("List of flights///");
		List<String> flights = null;

		Queue<City> doTheseCities = new LinkedList<City>();

		City currCity, nextCity;

		City finalCity = null;
		Flight nextFlight;

		try {
			currCity = cloneCityObject(startCity.getCode());

			finalCity = cloneCityObject(startCity.getCode());

			List<Flight> allInterMediateFlights = new ArrayList<>();

			List<FlightRoute> routes = new ArrayList<>();
			for (String c : startCity.getAdjacentCities()) {
				log.info("adjacent City " + c);
//				allInterMediateFlights.addAll(cityMap.get(c).getFlights());
				for (Flight f : cityMap.get(c).getFlights()) {
					if (destCity.getCode().equalsIgnoreCase(f.getDest())) {
						allInterMediateFlights.add(f);
					}
				}
			}
			allInterMediateFlights.sort(Comparator.comparing(Flight::getTimeArrive));

			log.info(mapper.writeValueAsString(allInterMediateFlights));

			for (Flight f : startCity.getFlights()) {

				if (destCity.getCode().equalsIgnoreCase(f.getDest())) {
					log.info("Found destination " + f.getFlightNum());
					Stack<Flight> fstack = new Stack<>();
					fstack.add(f);
					routes.add(FlightRoute.builder().flights(fstack).flightTime(f.getFlightTime()).build());
				} else {
					log.info("Checking intermediate ..." + f.getDest());
					for (Flight f2 : allInterMediateFlights) {

						if (f.dest.equalsIgnoreCase(f2.source)
								&& getWaitingTime(f.timeArrive, f2.timeDepart) > SHORTEST_TIME_TO_CHANGE_PLANES) {
							log.info("Found intermediate ..." + f.flightNum + f.dest + f2.flightNum);
							List<Flight> fstack = new Stack<>();
							fstack.add(f);
							fstack.add(f2);
							routes.add(FlightRoute
									.builder()
									.intermediateCity(f.dest)
									.waitingtime(getWaitingTime(f.timeArrive, f2.timeDepart))
									.flights(fstack).flightTime(f.getFlightTime()
											+ getWaitingTime(f.timeArrive, f2.timeDepart) + f2.getFlightTime())
									.build());
							
						}

					}

				}
			}

			Collections.sort(routes);

			Set<String> intermediateCities = new HashSet<>();
			
			List<Map<String, Map<String, Object>>> resultList = new ArrayList<>();
			int counter = 0;
			for(FlightRoute r : routes) {
				if(!intermediateCities.contains(r.getIntermediateCity())){
					
					intermediateCities.add(r.getIntermediateCity());
					
					Map<String, Map<String, Object>> route = new HashMap<>();
					
					String key = "";
					
					
					if(StringUtils.hasText(r.getIntermediateCity())) {
						key= Arrays.asList(startCity.getCode(), r.getIntermediateCity(), destCity.getCode()).stream()
								  .collect(Collectors.joining("_"));
					}else {
						key= Arrays.asList(startCity.getCode(), destCity.getCode()).stream()
								  .collect(Collectors.joining("_"));
					}
					
					
					String flightNums = r.getFlights().stream()
								.map(Flight::getFlightNum)
							  .collect(Collectors.joining("_"));
					
					route.put(key, Map.of(flightNums, r.getFlightTime()));
					
					if(counter<limit) {
						counter++;
					}else {
						break;
					}
					
					resultList.add(route);
					
				}
				
			}
			
			
			log.info("==xxxxxx===");
			log.info(mapper.writeValueAsString(resultList));
			
			result = mapper.writeValueAsString(resultList);

			log.info("==xxxxxx===");

		} catch (JsonProcessingException e) {
			log.error("Json Processing error: " + e.getMessage());
		}
		return result;

	}

	public List<String> listOfFlightsv1(City startCity, City destCity) {

		log.info("List of flights///");
		List<String> flights = null;

		Queue<City> doTheseCities = new LinkedList<City>();

		City currCity, nextCity;

		City finalCity = null;
		Flight nextFlight;

		PriorityQueue<City> adjacentNodes = new PriorityQueue<City>();

		HashMap<String, City> visitedCities = new HashMap<>();
		boolean isIntermediateFlight = false;
		try {
			currCity = cloneCityObject(startCity.getCode());

			finalCity = cloneCityObject(startCity.getCode());

			currCity.setCostFromStart(0);
			currCity.setPrevCity(null);

			adjacentNodes.add(currCity);
			while (!adjacentNodes.isEmpty()) {
				log.info("----");
				currCity = adjacentNodes.poll();

				if (visitedCities.containsKey(currCity.getCode())) {
					log.info("City is already visited " + currCity.getCode());
					continue;
				} else {
					log.info("Checking for city " + currCity.getCode() + " - current cost "
							+ currCity.getCostFromStart());
					visitedCities.put(currCity.getCode(), currCity);
				}

				Map<String, Flight> destinationCityShortestFlightMap = new HashMap<>();

				for (Flight f : currCity.getFlights()) {

					log.info("Flight " + f.getFlightNum() + " " + f.getDest());

					if (destinationCityShortestFlightMap.containsKey(f.getDest())) {
						Flight existingFlightToDest = destinationCityShortestFlightMap.get(f.getDest());

						if (f.getFlightTime() < existingFlightToDest.getFlightTime()) {
							log.info("Updating a shorter flight");
							destinationCityShortestFlightMap.put(f.getDest(), f);
						} else if (f.getFlightTime() == existingFlightToDest.getFlightTime()) {
							log.info("Updating a earlier time flight");
							destinationCityShortestFlightMap.put(f.getDest(),

									(f.getTimeDepart() < existingFlightToDest.getTimeDepart() ? f
											: existingFlightToDest)

							);
						}
					} else {
						destinationCityShortestFlightMap.put(f.getDest(), f);
					}

				}

				log.info(mapper.writeValueAsString(destinationCityShortestFlightMap));
				log.info("-----");

				for (String citycode : destinationCityShortestFlightMap.keySet()) {

					int cost = currCity.getCostFromStart()
							+ destinationCityShortestFlightMap.get(citycode).getFlightTime();

					if (isIntermediateFlight) {
						cost += SHORTEST_TIME_TO_CHANGE_PLANES;
					}

					nextCity = cloneCityObject(citycode);
					nextCity.setCostFromStart(cost);
					List<City> prevCities = nextCity.getPrevCity();

					if (prevCities == null) {
						prevCities = new ArrayList<>();
					}
					prevCities.add(currCity);

					List<String> prevFlights = nextCity.getPrevFlight();

					if (prevFlights == null) {
						prevFlights = new ArrayList<>();
					}
					prevFlights.add(destinationCityShortestFlightMap.get(citycode).getFlightNum());

					nextCity.setPrevCity(prevCities);
					nextCity.setPrevFlight(prevFlights);
					adjacentNodes.add(nextCity);
				}

				// Update isDirectFlight flag - set to false to calculate layover time
				isIntermediateFlight = true;

				log.info("=====");
//				log.info(mapper.writeValueAsString(adjacentNodes));
				log.info("=====");
			}

			log.info("==xxxxxx===");
////			log.info(mapper.writeValueAsString(visitedCities));
//			log.info(visitedCities.containsKey(destCity.getCode()) + " contains -");
//
//			if (visitedCities.containsKey(destCity.getCode())) {
//				log.info(" ---   PATH --- " + destCity.getCode());
//				printPath(visitedCities.get(destCity.getCode()));
//			}

			log.info("==xxxxxx===");

		} catch (JsonProcessingException e) {
			log.error("Json Processing error: " + e.getMessage());
		}
		return flights;

	}

	private void printPath(City city) {

		if (city != null && city.getPrevCity() != null) {

			for (City c : city.getPrevCity()) {

				log.info("Prev City of " + city.getCode());
				printPath(c);
			}
		}

	}

	public boolean containsCityByCode(String code) {
		return cityMap.containsKey(code);
	}

	public City getOrBuildCity(String code) {
		if (containsCityByCode(code)) {
			return getCity(code);
		} else {
			City c = City.builder().code(code).flights(new ArrayList<>()).adjacentCities(new HashSet<>()).build();
			addCity(c);
			return c;
		}
	}

	private City getCity(String code) {
		return cityMap.get(code);
	}

	private City cloneCityObject(String code) throws JsonMappingException, JsonProcessingException {
		return mapper.readValue(mapper.writeValueAsString(cityMap.get(code)), City.class);
	}

	public void addCity(City newCity) {
		if (containsCityByCode(newCity.getCode())) {
			System.out.println("City already in the graph.");
		} else {
			cityMap.put(newCity.getCode(), newCity);
			size++;
		}
	}

	public void addFlight(String flightNum, City startCity, City destCity, int clockTimeDepart, int clockTimeArrive) {
		int minuteTimeDepart = clockTimeToMinuteTime(clockTimeDepart);
		int minuteTimeArrive = clockTimeToMinuteTime(clockTimeArrive);
		Flight newFlight = Flight.builder().dest(destCity.getCode()).source(startCity.getCode())
				.timeArrive(minuteTimeArrive).timeDepart(minuteTimeDepart).flightNum(flightNum)
				.flightTime(getWaitingTime(minuteTimeDepart, minuteTimeArrive)).build();

		startCity.getFlights().add(newFlight);
		startCity.getAdjacentCities().add(destCity.getCode());
		destCity.getAdjacentCities().add(startCity.getCode());
	}

	public int getSize() {
		return size;
	}

	/**
	 * Time utils
	 */

	public static int clockTimeToMinuteTime(int clockTime) {
		int minutesDigits = clockTime % 100;
		int hoursDigits = clockTime / 100;
		return hoursDigits * 60 + minutesDigits;
	}

	public static String minuteTimeToClockTime(int minuteTime) {
		// If minuteTime is negative, it refers to a time from the
		// previous day.
		while (minuteTime < 0) {
			minuteTime += MINUTES_IN_A_DAY;
		}

		if (minuteTime > MINUTES_IN_A_DAY) {
			minuteTime = minuteTime % (60 * 24);
		}

		String clockTime;
		boolean pm = false;
		int numHours = minuteTime / 60;
		int numMins = minuteTime % 60;

		if (numHours > 11) {
			pm = true;
		}
		if (numHours > 12) {
			numHours = numHours % 12;
		}
		if (numHours == 0) {
			numHours = 12;
		}

		clockTime = "" + numHours + ":";
		if (numMins < 10) {
			clockTime = clockTime.concat("0");
		}
		clockTime = clockTime.concat("" + numMins);
		if (!pm) {
			clockTime = clockTime.concat(" am");
		} else {
			clockTime = clockTime.concat(" pm");
		}

		return clockTime;
	}

	public static String minuteTimeToHoursAndMinutes(int minuteTime) {
		if (minuteTime < 0) {
			System.out.println("Oops. You can't have negative time!");
			return null;
		}

		int numHours = minuteTime / 60;
		int numMins = minuteTime % 60;

		return "" + numHours + " hrs, " + numMins + " mins";
	}

	public static int getWaitingTime(int timeDepart, int timeArrive) {
		int waitingTime;
		if (timeArrive >= timeDepart) {
			waitingTime = timeArrive - timeDepart;
		} else {
			waitingTime = MINUTES_IN_A_DAY + timeArrive - timeDepart;
		}
		return waitingTime;
	}
}
