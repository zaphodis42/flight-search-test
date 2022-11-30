package in.devkiranpatil.test.dto;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class City implements Comparable {
	
	private String code;
	
	private Set<String> adjacentCities;
	private List<Flight> flights;

//	public Set<String> prevcities;
//	public Set<Flight> prevflightNumbers;
//	public int timeDepartPrevCity;
//	public int timeArriveThisCity;
	public int costFromStart;
	public int distFromStart;
	public boolean alreadyVisited;
	public List<City> prevCity;
	public List<String> prevFlight;

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		return Objects.equals(code, other.code);
	}

	@Override
	public int compareTo(Object other) {
		return costFromStart - ((City) other).costFromStart;
	}
	
	
}
