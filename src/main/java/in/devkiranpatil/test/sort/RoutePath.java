package in.devkiranpatil.test.sort;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutePath implements Comparable {

	private String destCityCode;

	private String previousCityCode;
	private String previousFlightCode;

	private int shortestCostFromOrigin;
	private boolean visited;

	@Override
	public int compareTo(Object o) {
		return this.getShortestCostFromOrigin() - ((RoutePath) o).getShortestCostFromOrigin();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutePath other = (RoutePath) obj;
		return Objects.equals(destCityCode, other.destCityCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(destCityCode);
	}

}
