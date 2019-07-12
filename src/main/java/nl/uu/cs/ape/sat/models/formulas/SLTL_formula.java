package nl.uu.cs.ape.sat.models.formulas;

import java.util.List;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.ModuleState;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AtomMapping;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.constructs.Predicate;

/**
 * The class is used to represent general SLTL constraints and to generate the
 * corresponding CNF representation. The form currently supports only trivial
 * formulas under the modal quantifier (atomic type and module formulas).
 * 
 * @author VedranPC
 *
 */
public abstract class SLTL_formula {

	private Predicate predicate;
	/*
	 * Sign of the predicate, <b>false</b> if the predicate is negated, <b>true</b>
	 * otherwise
	 */
	private boolean sign;

	public SLTL_formula(Predicate predicate) {
		this.predicate = predicate;
		sign = true;
	}

	/**
	 * Modal operators are performed over <b>formulas</b>. In case of value of
	 * <b>sign</b> being true, <b>formula</b> is positive, otherwise the formula is
	 * negative (<b>predicate</b> is negated).
	 * 
	 * @param predicate
	 * @param sign
	 */
	public SLTL_formula(boolean sign, Predicate predicate) {
		this.predicate = predicate;
		this.sign = sign;
	}

	/**
	 * Setting whether the predicate is negated or not. If sign is <b>false</b> the
	 * predicate will be negated.
	 * 
	 * @param sign
	 */
	public void setSign(boolean sign) {
		this.sign = sign;
	}

	/**
	 * Returns <b>false</b> if the predicate is negated, <b>true</b> otherwise.
	 * 
	 * @return
	 */
	public boolean getSign() {
		return sign;
	}

	/**
	 * Function returns the formula that was specified under the SLTL operator.<br/>
	 * Currently only predicates.
	 * 
	 * @return Predicate ({@link AbstractModule}, {@link Module} or {@link Type}).
	 */
	public Predicate getSubFormula() {
		return predicate;
	}

	/**
	 * Returns the type of the SLTL formula [<b>F</b>, <b>G</b> or <b>X</b>].
	 * 
	 * @return {@link String} [<b>F</b>, <b>G</b> or <b>X</b>], depending on the
	 *         type of SLTL formula.
	 */
	public abstract String getType();

	/**
	 * Generate String representation of the CNF formula for
	 * defined @moduleAutomaton and @typeAutomaton.
	 * 
	 * @param moduleAutomaton - automaton of all the module states
	 * @param typeAutomaton   - automaton of all the type states
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public abstract String getCNF(ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings);

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If <b>if_predicate</b> is used, tool <b>then_predicate</b> has to be used
	 * subsequently.
	 * 
	 * @param if_predicate    - predicate that enforce the usage of
	 *                        <b>then_predicate</b>
	 * @param then_predicate  - predicate that is enforced by <b>if_predicate</b>
	 * @param allModules      - list of all the modules
	 * @param moduleAutomaton - module automaton
	 * @param typeAutomaton   - type automaton
	 * @param mappings        - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String ite_module(Predicate if_predicate, Predicate then_predicate, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints += "-"
					+ mappings.add(if_predicate.getPredicate(), moduleAutomaton.getModuleStates().get(i).getStateName())
					+ " ";
			for (int j = i + 1; j < automatonSize; j++) {
				constraints += mappings.add(then_predicate.getPredicate(), moduleAutomaton.get(j).getStateName()) + " ";
			}
			constraints += "0\n";
		}
		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If type <b>if_predicate</b> is used, type <b>then_predicate</b> has to be
	 * used subsequently.
	 * 
	 * @param if_predicate    - predicate that enforce the usage of
	 *                        <b>then_predicate</b>
	 * @param then_predicate  - predicate that is enforced by <b>if_predicate</b>
	 * @param allModules      - list of all the modules
	 * @param moduleAutomaton - module automaton
	 * @param typeAutomaton   - type automaton
	 * @param mappings        - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String ite_type(Predicate if_predicate, Predicate then_predicate, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int numberOfBlocks = typeAutomaton.getUsedTypesBlocks().size();
		int numberOfStates = typeAutomaton.getUsedTypesBlocks().get(0).getBlockSize();
		for (int i_block = 0; i_block < numberOfBlocks; i_block++) {
			for (int i_state = 0; i_state < numberOfStates; i_state++) {
				/*
				 * If if_predicate is used in any state of a certain block
				 */
				constraints += "-" + mappings.add(if_predicate.getPredicate(),
						typeAutomaton.getUsedTypesBlock(i_block).getState(i_state).getStateName()) + " ";
				/*
				 * then then_predicate must be used in a state of the subsequent blocks.
				 */
				for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
					for (int j_state = i_state + 1; j_state < numberOfBlocks; j_state++) {
						constraints += mappings.add(then_predicate.getPredicate(),
								typeAutomaton.getUsedTypesBlock(j_block).getState(j_state).getStateName()) + " ";
					}

				}
				constraints += "0\n";
			}
		}
		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * If <b>if_predicate</b> is used, tool <b>then_predicate</b> cannot be used
	 * subsequently.
	 * 
	 * @param if_predicate       - predicate that forbids the usage of
	 *                           <b>then_not_predicate</b>
	 * @param then_not_predicate - module that is forbidden by <b>if_predicate</b>
	 * @param allModules         - list of all the modules
	 * @param moduleAutomaton    - module automaton
	 * @param typeAutomaton      - type automaton
	 * @param mappings           - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String itn_module(Predicate if_predicate, Predicate then_not_predicate,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize - 1; i++) {
			ModuleState currModuleState = moduleAutomaton.getModuleStates().get(i);
			for (int j = i + 1; j < automatonSize; j++) {
				constraints += "-" + mappings.add(if_predicate.getPredicate(), currModuleState.getStateName()) + " ";
				constraints += "-"
						+ mappings.add(then_not_predicate.getPredicate(), moduleAutomaton.get(j).getStateName())
						+ " 0\n";
			}
		}

		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If type <b>if_predicate</b> is used, then do not use type
	 * <b>then_not_predicate</b> subsequently.
	 * 
	 * @param if_predicate       - predicate that forbids the usage of
	 *                           <b>then_not_predicate</b>
	 * @param then_not_predicate - predicate that is forbidden by
	 *                           <b>if_predicate</b>
	 * @param allModules         - list of all the modules
	 * @param moduleAutomaton    - module automaton
	 * @param typeAutomaton      - type automaton
	 * @param mappings           - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String itn_type(Predicate if_predicate, Predicate then_not_predicate, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int numberOfBlocks = typeAutomaton.getUsedTypesBlocks().size();
		int numberOfStates = typeAutomaton.getUsedTypesBlocks().get(0).getBlockSize();
		for (int i_block = 0; i_block < numberOfBlocks - 1; i_block++) {
			for (int i_state = 0; i_state < numberOfStates; i_state++) {
				for (int j_block = i_block + 1; j_block < numberOfBlocks; j_block++) {
					for (int j_state = 0; j_state < numberOfStates; j_state++) {
						/*
						 * If if_predicate is used in any state of a certain block
						 */
						constraints += "-" + mappings.add(if_predicate.getPredicate(),
								typeAutomaton.getUsedTypesBlock(i_block).getState(i_state).getStateName()) + " ";
						/*
						 * then then_predicate cannot be used in a state of the subsequent blocks.
						 */
						constraints += "-" + mappings.add(then_not_predicate.getPredicate(),
								typeAutomaton.getUsedTypesBlock(j_block).getState(j_state).getStateName()) + " 0\n";
					}

				}
			}
		}
		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint::<br/>
	 * <br/>
	 * If we use module <b>second_module_in_sequence</b>, then we must have used
	 * <b>first_module_in_sequence</b> prior to it.
	 * 
	 * @param first_module_in_sequence  - predicate that is enforced by
	 *                                  <b>second_predicate</b>
	 * @param second_module_in_sequence - predicate that enforces the usage of
	 *                                  <b>first_predicate</b>
	 * @param allModules                - list of all the modules
	 * @param moduleAutomaton           - module automaton
	 * @param typeAutomaton             - type automaton
	 * @param mappings                  - set of the mappings for the literals
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String depend_module(Predicate second_module_in_sequence, Predicate first_module_in_sequence,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints += "-" + mappings.add(second_module_in_sequence.getPredicate(),
					moduleAutomaton.getModuleStates().get(i).getStateName()) + " ";
			for (int j = 0; j < i; j++) {
				constraints += mappings.add(first_module_in_sequence.getPredicate(),
						moduleAutomaton.get(j).getStateName()) + " ";
			}
			constraints += "0\n";
		}
		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint: :<br/>
	 * <br/>
	 * If we use predicate <b>first_module_in_sequence</b>, then use
	 * <b>second_module_in_sequence</b> as a next predicate in the sequence.
	 * 
	 * @param first_module_in_sequence  - predicate that enforce the usage of
	 *                                  <b>second_predicate</b>
	 * @param second_module_in_sequence - predicate that is enforced by
	 *                                  <b>first_predicate</b>
	 * @param allModules                - list of all the modules
	 * @param moduleAutomaton           - module automaton
	 * @param typeAutomaton             - type automaton
	 * @param mappings                  - set of the mappings for the literals
	 * @return {@link String} CNF representation of the constraint
	 */
	public static String next_module(Predicate first_module_in_sequence, Predicate second_module_in_sequence,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints += "-" + mappings.add(first_module_in_sequence.getPredicate(),
					moduleAutomaton.getModuleStates().get(i).getStateName()) + " ";
			/*
			 * Clause that forbids using first_predicate as the last in the sequence
			 */
			if (i < automatonSize - 1) {
				constraints += mappings.add(second_module_in_sequence.getPredicate(),
						moduleAutomaton.get(i + 1).getStateName()) + " ";
			}
			constraints += "0\n";
		}
		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint: :<br/>
	 * <br/>
	 * If we use predicate <b>first_predicate</b>, then use <b>second_predicate</b>
	 * as a next predicate in the sequence.
	 * 
	 * @param first_predicate  - predicate that enforce the usage of
	 *                         <b>second_predicate</b>
	 * @param second_predicate - predicate that is enforced by
	 *                         <b>first_predicate</b>
	 * @param allModules       - list of all the modules
	 * @param moduleAutomaton  - module automaton
	 * @param typeAutomaton    - type automaton
	 * @param mappings         - set of the mappings for the literals
	 * @return {@link String} CNF representation of the constraint
	 */
	public static String prev_module(Predicate second_module_in_sequence, Predicate first_module_in_sequence,
			ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";
		int automatonSize = moduleAutomaton.getModuleStates().size();
		for (int i = 0; i < automatonSize; i++) {
			constraints += "-" + mappings.add(second_module_in_sequence.getPredicate(),
					moduleAutomaton.getModuleStates().get(i).getStateName()) + " ";
			/*
			 * Clause that forbids using second_module_in_sequence as the first tool in the
			 * sequence
			 */
			if (i > 0) {
				constraints += mappings.add(first_module_in_sequence.getPredicate(),
						moduleAutomaton.get(i - 1).getStateName()) + " ";
			}
			constraints += "0\n";
		}
		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * Use <b>last_module</b> as last module in the solution.
	 * 
	 * @param last_module     - the module
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String useAsLastModule(AbstractModule last_module, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

		List<ModuleState> moduleAutomatonStates = moduleAutomaton.getModuleStates();
		ModuleState lastModuleState = moduleAutomatonStates.get(moduleAutomatonStates.size() - 1);
		constraints += mappings.add(last_module.getPredicate(), lastModuleState.getStateName()) + " 0\n";

		return constraints;
	}

	/**
	 * Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * Use <b>module</b> as the <b>n</b>-th module in the solution.
	 * 
	 * @param module          - the module
	 * @param n               - absolute position in the solution
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String useAsNthModule(AbstractModule module, int n, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
		String constraints = "";

		List<ModuleState> moduleAutomatonStates = moduleAutomaton.getModuleStates();
		ModuleState nthModuleState = moduleAutomatonStates.get(n - 1);
		constraints += mappings.add(module.getPredicate(), nthModuleState.getStateName()) + " 0\n";

		return constraints;
	}

	/**
	 * TODO: Creates a CNF representation of the Constraint:<br/>
	 * <br/>
	 * Use <b>module</b> in the solution exactly <b>n</b> times.
	 * 
	 * @param module          - module to be used
	 * @param n               - number of repetitions
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return {@link String} CNF representation of the SLTL formula
	 */
	public static String useModuleNtimes(AbstractModule module, int n, ModuleAutomaton moduleAutomaton,
			TypeAutomaton typeAutomaton, AtomMapping mappings) {
//		String constraints = "";
//
//		List<ModuleState> moduleAutomatonStates = moduleAutomaton.getModuleStates();
//		ModuleState nthModuleState = moduleAutomatonStates.get(index - 1);
//		constraints += mappings.add(module.getPredicate(), nthModuleState.getStateName()) + " 0\n";

		return null;
	}

}
