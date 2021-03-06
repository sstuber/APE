package nl.uu.cs.ape.sat.models.logic.constructs;

import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.PredicateLabel;

/**
 *  The {@code Atom} class represents elements of the workflow, that consists of the operation or type used, state where it is used
 *  and potentially a state that it refers to, i.e. input type elements refer to a state when the type was created.
 * 
 * @author Vedran Kasalica
 *
 */
public class Atom {

	/**  PredicateLabel that is referred (tool or type). */
	private final PredicateLabel predicate;
	/**  State in which the type/operation was used. */
	private final State argumentState;
	/**  Defines the type of the element in the workflow that the atom describes (tool, memory type, etc.) */
	private WorkflowElement elementType;
	
	
	/**
	 * Creates an atom that can represent usage of the tool, creation or usage of a type, or a reference between an input type and the state in which it was generated..
	 * @param predicate - predicate used
	 * @param usedInState - state in the automaton it was used/created in
	 * @param elementType - element that describes what type of a predicate is described
	 */
	public Atom(PredicateLabel predicate, State usedInState, WorkflowElement elementType) {
		this.predicate = predicate;
		this.argumentState = usedInState;
		this.elementType = elementType;
	}
	
	
	/**
	 * Creates a state in the automaton that corresponds to a usage of a data type as input, by a tool.
	 * @param atom - atom that is being copied.
	 */
	public Atom(Atom atom) {
		this.predicate = atom.predicate;
		this.argumentState = atom.argumentState;
		this.elementType = atom.elementType;
	}
	
    
	/** @return Object {@link #predicate}. */
	public PredicateLabel getPredicate() {
		return predicate;
	}

	@Override
	public int hashCode() {
		return predicate.hashCode() + argumentState.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Atom other = (Atom) obj;
		
		return this.predicate.equals(other.getPredicate()) && this.argumentState.equals(other.getUsedInStateArgument());
	}
	
	/** @return Object {@link #argumentState}. */
	public State getUsedInStateArgument() {
		return argumentState;
	}

	/**
	 * Return the type of the element in the workflow (tool, memory type, etc.)
	 * @return The {@link WorkflowElement} that corresponds to the atom usage.
	 */
	public WorkflowElement getWorkflowElementType() {
		return elementType;
	}


	/**
	 * Returns the string representation of the WorkflowElement, used for the textual solution representation. In case of the atom depicting
	 * a usage of a type in the workflow, the structure of the representation contains an additional attribute, state in which the type was initially added to the memory.
	 * @return String representing the workflow element in a textual form.
	 */
	public String toString() {
		if(this.elementType == WorkflowElement.MEM_TYPE_REFERENCE) {
			return "[" + predicate.getPredicateID() + "] <- ("+ argumentState.getPredicateID() + ")";
		} else {
			return predicate.getPredicateID() + "(" + argumentState.getPredicateID() + ")";
		}
	}
	
 
	
}
