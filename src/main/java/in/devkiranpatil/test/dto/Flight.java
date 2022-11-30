package in.devkiranpatil.test.dto;

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
public class Flight {

	public String flightNum;
	public String source;
	public String dest;
	public int timeDepart;
	public int timeArrive;
	public int flightTime;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Flight other = (Flight) obj;
		return Objects.equals(flightNum, other.flightNum);
	}
	@Override
	public int hashCode() {
		return Objects.hash(flightNum);
	}
}
