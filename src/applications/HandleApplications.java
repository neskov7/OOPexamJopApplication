package applications;

import java.util.*;
import java.util.stream.Collectors;

public class HandleApplications {
	
	private Map<String,Skill> skills = new HashMap<String, Skill>();
	private Map<String, Position> positions = new HashMap<String, Position>();
	private Map<String, Applicant> applicants = new HashMap<String, Applicant>();


	public void addSkills(String... names) throws ApplicationException {
		for (String n : names) {
			if (skills.containsKey(n)){
				throw new ApplicationException();
			}else{
				skills.put(n, new Skill(n));
			}
		}
	}
	public void addPosition(String name, String... skillNames) throws ApplicationException {
		Map<String, Skill> requirements = new HashMap<String, Skill>();
		if (positions.containsKey(name)){
			throw new ApplicationException();
		}
		for (String skillName : skillNames) {
			if(!skills.containsKey(skillName)){
				throw new ApplicationException();
			}else{
				requirements.put(skillName, skills.get(skillName));
			}
		}
		positions.put(name, new Position(name, requirements.values()));
	}
	
	public Skill getSkill(String name) {return skills.get(name);}
	
	public Position getPosition(String name) {return positions.get(name);}
	
	public void addApplicant(String name, String capabilities) throws ApplicationException {
		Map<String, Skill> appSkills = new HashMap<String, Skill>();
		Map<String, Integer> skillLv = new HashMap<String, Integer>();
		if(applicants.containsKey(name)){
			throw new ApplicationException();
		}
		String[] splittedStrings = capabilities.split(",");
		for (String capability : splittedStrings) {
			String[] ss = capability.split(":");
			if (!skills.containsKey(ss[0])){throw new ApplicationException();}
			Skill skill = skills.get(ss[0]);
			int lv = new Integer(ss[1]);
			if (lv < 1 || lv > 10) {throw new ApplicationException();}
			appSkills.put(skill.getName(), skill);
			skillLv.put(skill.getName(), lv);
		}
		applicants.put(name,new Applicant(name, appSkills, skillLv));
	}
	
	public String getCapabilities(String applicantName) throws ApplicationException {
		if(!applicants.containsKey(applicantName)){throw new ApplicationException();}
		Set<String> resultSet = applicants.get(applicantName).getskills().stream()
		.sorted(Comparator.comparing(Skill::getName))
		.map(Skill::getName)
		.collect(Collectors.toSet());
		if (resultSet.isEmpty()) {
			return "";
		}
		
		StringBuilder resultString = new StringBuilder();
		Applicant applicant = applicants.get(applicantName);
		resultSet.forEach( s -> {
			String string = s + ":" + applicant.getLvlsMap().get(s);
			if (resultString.length() != 0) resultString.append(",");
			resultString.append(string);
		} );
		
		return resultString.toString();
	}
	
	public void enterApplication(String applicantName, String positionName) throws ApplicationException {
		if (!applicants.containsKey(applicantName)) { throw new ApplicationException();	}
		if (!positions.containsKey(positionName)) { throw new ApplicationException(); }
		if (applicants.get(applicantName).getRequestedPosition() != null ) {throw new ApplicationException();}
		Applicant applicant = applicants.get(applicantName);
		Position position = positions.get(positionName);
		if(!position.checkSkills(applicant.getSkillsMap())){throw new ApplicationException();}
		position.addApplicant(applicant);
	}
	
	public int setWinner(String applicantName, String positionName) throws ApplicationException {
		Position position = positions.get(positionName);
		if(!position.checkApplicant(applicantName)) throw new ApplicationException();
		if(position.getWinner()!=null) throw new ApplicationException();
		
		int n = position.checkPossibleWinner(applicantName);
		if( n <= position.getSkillsNumber()*6) throw new ApplicationException();
		position.setWinner(applicantName);
		return n;
	}
	
	public SortedMap<String, Long> skill_nApplicants() {
		
		Map<String,Long > tmp = 
				applicants.values().stream()
				.flatMap(a -> a.getskills().stream())
				.sorted(Comparator.comparing(Skill::getName))
				.collect(Collectors.groupingBy(Skill::getName, Collectors.counting()));

		return new TreeMap<String, Long>(tmp);
	}
	public String maxPosition() {
		Optional<Position> result = 
		positions.values().stream()
		//.sorted(Comparator.comparing(Position::getApplicantsNumber, Comparator.reverseOrder()))
		.max(Comparator.comparing(Position::getApplicantsNumber));
		
		try {
			return result.get().getName();
		} catch (NoSuchElementException e) {
			System.err.println("max has returned null element");
			return null;
		}
		
	}
}

