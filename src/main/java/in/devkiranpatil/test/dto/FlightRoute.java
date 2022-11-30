package in.devkiranpatil.test.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightRoute implements Comparable{

	List<Flight> flights;
	String intermediateCity;
	int flightTime;
	int waitingtime;
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return this.flightTime - ((FlightRoute)o).flightTime;
	}
}
