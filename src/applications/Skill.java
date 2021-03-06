package applications;

import java.util.*;
import java.util.stream.Collectors;


public class Skill {

	private String name;
	private Map<String, Position> positions = new HashMap<>();
	private int numeroRichiedenti = 0;
	
	public Skill(String name){
		this.name = name;
	}
	
	public String getName() {return name;}
	
	public List<Position> getPositions() {
		return 
				positions.values().stream()
				.sorted(Comparator.comparing(Position::getName))
				.collect(Collectors.toList())
				;		
	}
	
	public void addPosition(Position position){
		positions.put(position.getName(), position);
	}
}