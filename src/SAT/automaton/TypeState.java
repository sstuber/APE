package SAT.automaton;

public class TypeState implements State {

	private String stateName;
	private int stateNumber;
	
	public TypeState(String stateName, int stateNumber) {
		super();
		this.stateName = stateName;
		this.stateNumber = stateNumber;
	}


	@Override
	public String getStateName() {
		return stateName;
	}


	public void setStateName(String stateName) {
		this.stateName = stateName;
	}


	public int getStateNumber() {
		return stateNumber;
	}


	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}
	
	
	
}
