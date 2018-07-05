package SAT.models;

import SAT.automaton.ModuleAutomaton;
import SAT.automaton.TypeAutomaton;

public class SLTL_formula_ITE extends SLTL_formula {

	public SLTL_formula_ITE(Atom atom) {
		super(atom);
	}
	
	public SLTL_formula_ITE(Atom atom, boolean negated) {
		super(atom, negated);
	}

	@Override
	public String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "X";
	}

}
