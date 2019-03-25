package nl.uu.cs.ape.sat.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Block of Type states that comprise the Type automaton.
 * @author vedran
 *
 */
public class TypeBlock {

	private List<TypeState> typeStates;
	private int blockNumber;
	private int blockSize;

	public TypeBlock(int blockNumber) {
		typeStates = new ArrayList<TypeState>();
		this.blockNumber = blockNumber;
		this.blockSize = 0;
	}

	public TypeBlock(List<TypeState> typeStates, int blockNumber) {
		super();
		this.typeStates = typeStates;
		this.blockNumber = blockNumber;
		this.blockSize = typeStates.size();
	}

	public List<TypeState> getTypeStates() {
		return typeStates;
	}

	public void setTypeStates(List<TypeState> typeStates) {
		this.typeStates = typeStates;
	}

	/**
	 * Return the ordering number of the block in the Type automaton.
	 * 
	 * @return Ordering number of the block
	 */
	public int getBlockNumber() {
		return blockNumber;
	}

	/**
	 * Return the block size.
	 * 
	 * @return Block size.
	 */
	public int getBlockSize() {
		return blockSize;
	}

	/**
	 * Add Type state to the Type Block
	 * @param state - Type State to be added
	 */
	public void addState(TypeState state) {
		typeStates.add(state);
		this.blockSize++;
	}

}