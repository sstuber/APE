package nl.uu.cs.ape.sat.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.uu.cs.ape.sat.utils.APEConfig;
import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.core.implSAT.SAT_SynthesisEngine;
import nl.uu.cs.ape.sat.automaton.Block;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.Predicate;

/**
 * The {@code AllModules} class represent the set of all modules/tools that can
 * be part of our program. Each of them is either {@link Module} or
 * {@link AbstractModule}.
 * 
 * @author Vedran Kasalica
 *
 */
public class AllModules extends HashMap<String, AbstractModule>{

	/** Set of all the module IDs of the annotated modules in the domain. */
	private Set<String> annotatedModules;
	/** Root of the data taxonomy. */
	private String moduleTaxonomyRoot;

	public AllModules() {
		super();
		this.annotatedModules = new HashSet<String>();
		this.moduleTaxonomyRoot = APEConfig.getConfig().getTool_taxonomy_root();
	}

	/**
	 * Create a map-set containing all modules from @modules, omitting the
	 * duplicates.
	 * 
	 * @param modules
	 */
	public AllModules(Collection<? extends AbstractModule> modules) {
		super();
		for (AbstractModule module : modules) {
			this.addModule(module);
		}
	}

	/**
	 * Return the set of currently defined modules (both {@link AbstractModule} and {@link Module}).
	 * @return
	 */
	public Collection<AbstractModule> getModules() {
		return super.values();
	}

	/**
	 * Adds the specified element to this set if it is not already present (optional
	 * operation) and returns it. More formally, adds the specified element e to
	 * this set if the set contains no element e2 such that (e==null ? e2==null :
	 * e.equals(e2)). If this set already contains the element, the call leaves the
	 * set unchanged and returns the existing element. In combination with the
	 * restriction on constructors, this ensures that sets never contain duplicate
	 * elements. It also check whether the new element extends the existing one, it
	 * that case the existing one is replaced by the extended one.
	 * 
	 * @param module - The AbstractModule/Module that needs to be added.
	 * @return The element if it's a new one or the existing element if this set
	 *         contains the specified element.
	 */
	public AbstractModule addModule(AbstractModule module) {
		AbstractModule tmpModule = super.get(module.getPredicateID());
		if (module instanceof Module && (tmpModule != null)) {
			if (tmpModule instanceof Module) {
				return tmpModule;
			} else {
				Module newModule = new Module(((Module) module), tmpModule);
				/*
				 * swap the AbstractModule with the Module
				 */
				swapAbstractModule2Module(newModule, tmpModule);
				return module;
			}
		} else {
			if (tmpModule != null) {
				return tmpModule;
			} else {
				this.put(module.getPredicateID(), module);
				return module;
			}
		}
	}
	

	/**
	 * Removes the {@link AbstractModule} from the set of all modules and adds the
	 * {@link Module} element (or vice versa). Swaps the objects in the set of all
	 * Modules.
	 * 
	 * @param newModule - object that will be added
	 * @param oldModule - object that will be removed
	 */
	public void swapAbstractModule2Module(AbstractModule newModule, AbstractModule oldModule) {
		this.remove(oldModule.getPredicateID());
		this.put(newModule.getPredicateID(), newModule);
	}

	/**
	 * Returns the module to which the specified key is mapped to, or {@code null}
	 * if the moduleID has no mappings.
	 * 
	 * @param moduleID - the key whose associated value is to be returned
	 * @return {@link AbstractModule} or {@link Module} to which the specified key
	 *         is mapped to, or {@code null} if the moduleID has no mappings
	 */
	public AbstractModule get(String moduleID) {
		return super.get(moduleID);
	}

	/**
	 * Returns the root module of the taxonomy.
	 * 
	 * @return The root module.
	 */
	public AbstractModule getRootModule() {
		return this.get(moduleTaxonomyRoot);
	}
	
	/**
	 * Returns the ID of the root module of the taxonomy.
	 * 
	 * @return The root module.
	 */
	public String getRootID() {
		return moduleTaxonomyRoot;
	}

	/**
	 * Returns true if this set contains the specified element. More formally,
	 * returns true if and only if this set contains an element e such that (o==null
	 * ? e==null : o.equals(e)).
	 * 
	 * @param module
	 * @return true if this set contains the specified element
	 */
	public boolean existsModule(AbstractModule module) {
		return this.containsKey(module.getPredicateID());
	}

	public int size() {
		return this.size();
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
	 */
	private List<Pair> getToolPairs() {
		List<Pair> pairs = new ArrayList<Pair>();

		List<AbstractModule> iterator = new ArrayList<AbstractModule>();
		for (AbstractModule module : this.values()) {
			if (module.isTool())
				iterator.add(module);
		}

//		System.out.println(APEConfig.getConfig().getTool_taxonomy_root() + ": " + iterator.size());

		for (int i = 0; i < iterator.size() - 1; i++) {
			for (int j = i + 1; j < iterator.size(); j++) {

				pairs.add(new Pair(iterator.get(i), iterator.get(j)));
			}
		}

		return pairs;
	}

	/**
	 * Returns a list of pairs of tools from modules. Note that the abstract modules
	 * are not returned, only the unique pairs of modules that are representing
	 * actual tools.
	 * 
	 * @return list of pairs of modules
	 */
	private List<Pair> getPredicatePairs(List<? extends Predicate> predicateList) {
		List<Pair> pairs = new ArrayList<Pair>();

		for (int i = 0; i < predicateList.size() - 1; i++) {
			for (int j = i + 1; j < predicateList.size(); j++) {

				pairs.add(new Pair(predicateList.get(i), predicateList.get(j)));
			}
		}

		return pairs;
	}

	/**
	 * Return {@code true} if the module is annotated.
	 * 
	 * @param moduleID - ID of the module that is evaluated.
	 * @return {@code true} if the module is annotated, {@code false} otherwise.
	 */
	public boolean getIsAnnotatedModule(String moduleID) {
		return annotatedModules.contains(moduleID);
	}

	/**
	 * Adds the module to the set of annotated modules.
	 * 
	 * @param moduleID - ID of the module that is annotated.
	 */
	public void addAnnotatedModule(String moduleID) {
		annotatedModules.add(moduleID);
	}
	

	/**
	 * Generate constraints that ensure that the set of inputs correspond to the
	 * tool specifications.<br>
	 * Return the CNF representation of the input type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton. <br>
	 * <br>
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param emptyType       - represents absence of types
	 * @param mappings
	 * @return String representation of constraints
	 */
	private String inputCons(SAT_SynthesisEngine synthesisInstance) {

		StringBuilder constraints = new StringBuilder();
		AtomMapping mappings = synthesisInstance.getMappings();
		for (Entry<String, AbstractModule> mapModule : this.entrySet()) {
			AbstractModule module = mapModule.getValue();
			/* ..which is a Tool.. */
			if ((module instanceof Module)) {
				/* ..iterate through all the states.. */
				for (State moduleState : synthesisInstance.getModuleAutomaton().getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					/* ..and for each state and input state of that module state.. */
					List<State> currInputStates = synthesisInstance.getTypeAutomaton().getUsedTypesBlock(moduleNo - 1).getStates();
					List<DataInstance> moduleInputs = module.getModuleInput();
					for (State currInputState : currInputStates) {
						int currInputStateNo = currInputState.getStateNumber();
						/*
						 * ..require data type and/or format to be used in one of the directly preceding
						 * input states, if the data type/format it exists, otherwise use empty type.
						 */
						if (currInputStateNo < moduleInputs.size()) {
							/* Get input type and/or format that are/is required by the tool */
							for (Type currInputType : moduleInputs.get(currInputStateNo).getTypes()) {
								/* Encode: if module was used in the module state */
								constraints = constraints.append("-")
										.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
								/*
								 * .. the corresponding data and format types need to be provided in input
								 * states
								 */
								constraints = constraints
										.append(mappings.add(currInputType, currInputState, WorkflowElement.USED_TYPE))
										.append(" 0\n");
							}
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							constraints = constraints
									.append(mappings.add(synthesisInstance.getAllTypes().getEmptyType(), currInputState, WorkflowElement.USED_TYPE))
									.append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Constraints that ensure that the referenced memory states contain the same
	 * data type as the one that is used as the input for the tool. Constraints
	 * ensure that the {@link WorkflowElement#MEM_TYPE_REFERENCE} are implemented
	 * correctly.
	 * 
	 * @param allTypes
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representing the constraints required to ensure that the
	 *         {@link WorkflowElement#MEM_TYPE_REFERENCE} are implemented correctly.
	 */
	private String generalReferenceCons(AllTypes allTypes, TypeAutomaton typeAutomaton, AtomMapping mappings) {
		StringBuilder constraints = new StringBuilder();

		/* For each type instance */
		for (Type currType : allTypes.getTypes()) {
			if (currType.isSimpleType() || currType.isEmptyType()) {
				/* ..for each state in which type can be used .. */
				for (Block currUsedBlock : typeAutomaton.getUsedTypesBlocks()) {
					for (State currUsedTypeState : currUsedBlock.getStates()) {
						if (currType.isSimpleType()) {
							/* ..the referenced memory state cannot be null.. */
							constraints = constraints.append("-")
									.append(mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
									.append(" ");
							constraints = constraints.append("-").append(mappings.add(typeAutomaton.getNullState(),
									currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");

							/* ..and for each state in which type can be created in memory .. */
							for (Block memoryBlock : typeAutomaton.getMemoryTypesBlocks()) {
								for (State refMemoryTypeState : memoryBlock.getStates()) {
									/*
									 * If the type (currType) is used as an input for a tool (in state
									 * currUsedTypeState)..
									 */
									constraints = constraints.append("-").append(
											mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
											.append(" ");
									/*
									 * ..and the state is referencing a memory state where the type was created
									 * (refMemoryTypeState)
									 */
									constraints = constraints.append("-").append(mappings.add(refMemoryTypeState,
											currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" ");
									/* ..the type has to be generated in the the referenced memory type state. */
									constraints = constraints.append(
											mappings.add(currType, refMemoryTypeState, WorkflowElement.MEMORY_TYPE))
											.append(" 0\n");

								}
							}
							/* If the type is empty the referenced state has to be null. */
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(currType, currUsedTypeState, WorkflowElement.USED_TYPE))
									.append(" ");
							constraints = constraints.append(mappings.add(typeAutomaton.getNullState(),
									currUsedTypeState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the inputs are available in the memory.
	 * Memory in Message Passing Approach is limited to one the output of the
	 * previous tool. <br>
	 * Return the CNF representation of the input type constraints for all modules
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Message Passing Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	private String inputMsgPassingCons(TypeAutomaton typeAutomaton, AtomMapping mappings) {

		// setting up input constraints (Message Passing Approach)
		StringBuilder constraints = new StringBuilder();

		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {
				/* Used state can reference states that are directly preceding the state */
				List<State> possibleMemStates = typeAutomaton.getMemoryTypesBlock(blockNumber).getStates();
				possibleMemStates.add(typeAutomaton.getNullState());
				for (State exictingMemState : possibleMemStates) {
					constraints = constraints
							.append(mappings.add(exictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
				}
				constraints = constraints.append(" 0\n");

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair pair : getPredicatePairs(possibleMemStates)) {
					constraints = constraints.append("-")
							.append(mappings.add(pair.getFirst(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
					constraints = constraints.append("-")
							.append(mappings.add(pair.getSecond(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}
				/*
				 * Used state cannot reference states that are not directly preceding the state
				 */
				for (int notCurrBlockNumber = 0; notCurrBlockNumber < typeAutomaton.getMemoryTypesBlocks()
						.size(); notCurrBlockNumber++) {
					if (notCurrBlockNumber != blockNumber) {
						for (State nonExictingMemState : typeAutomaton.getMemoryTypesBlock(notCurrBlockNumber)
								.getStates()) {
							constraints = constraints.append("-").append(mappings.add(nonExictingMemState,
									currInputState, WorkflowElement.MEM_TYPE_REFERENCE)).append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * TODO: TEST THE METHOD!!
	 * Function returns the encoding that ensures that each time a memory type is
	 * referenced by a tool's input type, it has to be of right type. <br>
	 * Function is implementing the Message Passing Approach.
	 * 
	 * @param emptyType
	 * @param typeAutomaton
	 * @param mappings
	 * @param enforceUsageOfAllWorkflowInputTypes - true if all the inputs given to
	 *                                            the workflow should be used
	 * @param enforceUsageOfAllGeneratedTypes     - true if all the generated types
	 *                                            have to be used
	 * @return String representation of constraints.
	 */
	private String enforcingUsageOfGeneratedTypesMsgPassingCons(SAT_SynthesisEngine synthesisInstance) {

		AtomMapping mappings = synthesisInstance.getMappings();
		Type emptyType = synthesisInstance.getAllTypes().getEmptyType();
		
		
		StringBuilder constraints = new StringBuilder();
		String usageOfAllTypes = " ", usageOfAllWorkflowInputType = " ";
		String usageOfOneType = " 0\n", usageOfWorkflowInputType = " 0\n";
		if (synthesisInstance.getConfig().getUse_workflow_input() == ConfigEnum.ALL) {
			usageOfAllWorkflowInputType = " 0\n";
			usageOfWorkflowInputType = " ";
		}
		if (synthesisInstance.getConfig().getUse_all_generated_data()  == ConfigEnum.ALL) {
			
			usageOfAllTypes = " 0\n";
			usageOfOneType = " ";
		}
		/*
		 * Setting up the constraints that ensure usage of the generated types in the
		 * memory. (e.g.  all workflow inputs and at least one of each of the tool outputs
		 * needs to be used in the program, unless they are empty).
		 */
		for (Block currBlock : synthesisInstance.getTypeAutomaton().getMemoryTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currMemoryState : currBlock.getStates()) {
				/* If the memory is provided as input */
				if (blockNumber == 0) {
					if(synthesisInstance.getConfig().getUse_workflow_input()  == ConfigEnum.NONE) {
						continue;
					}
					constraints = constraints
							.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE)).append(" ");
					for (State inputState : synthesisInstance.getTypeAutomaton().getUsedTypesBlock(blockNumber).getStates()) {
						constraints = constraints
								.append(mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
								.append(usageOfAllWorkflowInputType);
					}
					constraints = constraints.append(usageOfWorkflowInputType);
				} else {
					if(synthesisInstance.getConfig().getUse_all_generated_data()  == ConfigEnum.NONE) {
						break;
					}
					constraints = constraints
							.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE)).append(" ");
					for (State inputState : synthesisInstance.getTypeAutomaton().getUsedTypesBlock(blockNumber).getStates()) {
						constraints = constraints
								.append(mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
								.append(usageOfAllTypes);
					}
					constraints = constraints.append(usageOfOneType);
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Generate constraints that ensure that the inputs are available in the memory.
	 * Memory in Shared Memory Approach contains outputs of all the previous tools.
	 * <br>
	 * Return the CNF representation of the input type constraints for all modules,
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton and
	 * the Shared Memory Approach.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param mappings
	 * @return String representation of constraints.
	 */
	private String inputSharedMemCons(TypeAutomaton typeAutomaton, AtomMapping mappings) {

		// setting up input constraints (Shared Memory Approach)
		StringBuilder constraints = new StringBuilder();

		for (Block currBlock : typeAutomaton.getUsedTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			for (State currInputState : currBlock.getStates()) {
				/*
				 * Used state can reference states that are currently in the shared memory, i.e.
				 * already created.
				 */
				List<State> possibleMemStates = typeAutomaton.getMemoryStatesUntilBlockNo(blockNumber);
				possibleMemStates.add(typeAutomaton.getNullState());
				for (State exictingMemState : possibleMemStates) {
					constraints = constraints
							.append(mappings.add(exictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
				}
				constraints = constraints.append(" 0\n");

				/* Defining that each input can reference only one state in the shared memory */
				for (Pair pair : getPredicatePairs(possibleMemStates)) {
					constraints = constraints.append("-")
							.append(mappings.add(pair.getFirst(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" ");
					constraints = constraints.append("-")
							.append(mappings.add(pair.getSecond(), currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}

				/*
				 * Used state cannot reference states that are yet to be created, i.e. not yet
				 * in the shared memory.
				 */
				for (State nonExictingMemState : typeAutomaton.getMemoryStatesAfterBlockNo(blockNumber)) {
					constraints = constraints.append("-").append(
							mappings.add(nonExictingMemState, currInputState, WorkflowElement.MEM_TYPE_REFERENCE))
							.append(" 0\n");
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Function returns the encoding that ensures that each time a memory type is
	 * referenced by a tool's input type, it has to be of right type. <br>
	 * Function is implementing the Shared Memory Approach.
	 * 
	 * @param emptyType
	 * @param typeAutomaton
	 * @param mappings
	 * @param enforceUsageOfAllWorkflowInputTypes - true if all the inputs given to
	 *                                            the workflow should be used
	 * @param enforceUsageOfAllGeneratedTypes     - true if all the generated types
	 *                                            have to be used
	 * @return String representation of constraints.
	 */
	private String enforcingUsageOfGeneratedTypesSharedMemCons(SAT_SynthesisEngine synthesisInstance) {

		AtomMapping mappings = synthesisInstance.getMappings();
		Type emptyType = synthesisInstance.getAllTypes().getEmptyType();
		TypeAutomaton typeAutomaton = synthesisInstance.getTypeAutomaton();
		StringBuilder constraints = new StringBuilder();
		/*
		 * Setting up the constraints that ensure usage of the generated types in the
		 * memory, (e.g. all workflow inputs and at least one of each of the tool outputs
		 * needs to be used in the program, unless they are empty.)
		 */
		for (Block currBlock : typeAutomaton.getMemoryTypesBlocks()) {
			int blockNumber = currBlock.getBlockNumber();
			/* If the memory is provided as input */
			if (blockNumber == 0) {
				/* In case that all workflow inputs need to be used */
				if (synthesisInstance.getConfig().getUse_workflow_input() == ConfigEnum.ALL) {
					for (State currMemoryState : currBlock.getStates()) {
						constraints = constraints
								.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
								.append(" ");
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
						constraints = constraints.append(" 0\n");
					}
					/* In case that at least one workflow input need to be used */
				} else if (synthesisInstance.getConfig().getUse_workflow_input() == ConfigEnum.ONE) {
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getStateNumber() == 0) {
							constraints = constraints
									.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
									.append(" ");
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {

							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
					}
					constraints = constraints.append(" 0\n");
				}
				/* In case that none of the workflow input has to be used, do nothing. */
			} else {
				/* In case that all generated data need to be used. */
				if (synthesisInstance.getConfig().getUse_all_generated_data() == ConfigEnum.ALL) {
					for (State currMemoryState : currBlock.getStates()) {
						constraints = constraints
								.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
								.append(" ");
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
						constraints = constraints.append(" 0\n");
					}
					/* In case that at least one of the generated data instances per tool need to be used. */
				} else if (synthesisInstance.getConfig().getUse_all_generated_data() == ConfigEnum.ONE) {
					for (State currMemoryState : currBlock.getStates()) {
						if (currMemoryState.getStateNumber() == 0) {
							constraints = constraints
									.append(mappings.add(emptyType, currMemoryState, WorkflowElement.MEMORY_TYPE))
									.append(" ");
						}
						for (State inputState : typeAutomaton.getUsedStatesAfterBlockNo(blockNumber - 1)) {
							constraints = constraints.append(
									mappings.add(currMemoryState, inputState, WorkflowElement.MEM_TYPE_REFERENCE))
									.append(" ");
						}
					}
					constraints = constraints.append(" 0\n");
				}
				/* In case that none generated data has to be used do nothing. */

			}
		}

		return constraints.toString();
	}

	/**
	 * Return the CNF representation of the output type constraints for all tools
	 * regarding @typeAutomaton, for the synthesis concerning @moduleAutomaton. <br>
	 * <br>
	 * Generate constraints that preserve tool outputs.
	 * 
	 * @param moduleAutomaton
	 * @param typeAutomaton
	 * @param emptyType       - represents absence of types
	 * @param mappings
	 * @return String representation of constraints
	 */
	private String outputCons(SAT_SynthesisEngine synthesisInstance) {

		AtomMapping mappings = synthesisInstance.getMappings();
		StringBuilder constraints = new StringBuilder();

		// for each module
		for (Entry<String, AbstractModule> mapModule : this.entrySet()) {
			AbstractModule module = mapModule.getValue();
			// that is a Tool
			if ((module instanceof Module)) {
				// iterate through all the states
				for (State moduleState : synthesisInstance.getModuleAutomaton().getModuleStates()) {
					int moduleNo = moduleState.getStateNumber();
					// and for each state and output state of that module state
					List<State> currOutputStates = synthesisInstance.getTypeAutomaton().getMemoryTypesBlock(moduleNo).getStates();
					List<DataInstance> moduleOutputs = module.getModuleOutput();
					for (int i = 0; i < currOutputStates.size(); i++) {
						if (i < moduleOutputs.size()) {
							for (Type outputType : moduleOutputs.get(i).getTypes()) { // set type and format for the
																						// single output
								// if module was used in the module state
								constraints = constraints.append("-")
										.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
								// require type and/or format to be used in one of the directly
								// proceeding output states if it exists, otherwise use empty type

								constraints = constraints.append(
										mappings.add(outputType, currOutputStates.get(i), WorkflowElement.MEMORY_TYPE))
										.append(" 0\n");
							}
						} else {
							constraints = constraints.append("-")
									.append(mappings.add(module, moduleState, WorkflowElement.MODULE)).append(" ");
							constraints = constraints.append(
									mappings.add(synthesisInstance.getAllTypes().getEmptyType(), currOutputStates.get(i), WorkflowElement.MEMORY_TYPE))
									.append(" 0\n");
						}
					}
				}
			}
		}

		return constraints.toString();
	}

	/**
	 * Return a CNF representation of the INPUT and OUTPUT type constraints.
	 * Depending on the parameter pipeline, the INPUT constraints will be based on a
	 * pipeline or general memory approach.
	 * 
	 * @param moduleAutomaton      - represents the module automaton
	 * @param typeAutomaton        - represent the type automaton
	 * @param shared_memory        - if false pipeline approach, otherwise the
	 *                             general memory approach is used
	 * @param emptyType            - represents absence of types
	 * @param mappings
	 * @param useAllWorkflowInputs - true if all the inputs given to the workflow
	 *                             should be used
	 * @param useAllGeneratedTypes - true if all the generated types have to be used
	 * @return {@link String} representation of constraints regarding the required
	 *         INPUT and OUTPUT types of the modules
	 */
	public String modulesConstraints(SAT_SynthesisEngine synthesisInstance) {
		StringBuilder constraints = new StringBuilder();
		constraints = constraints.append(inputCons(synthesisInstance));
		if (!synthesisInstance.getConfig().getShared_memory()) {
			constraints = constraints.append(inputMsgPassingCons(synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
			constraints = constraints.append(enforcingUsageOfGeneratedTypesMsgPassingCons(synthesisInstance));
		} else {
			constraints = constraints.append(inputSharedMemCons(synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));
			constraints = constraints.append(enforcingUsageOfGeneratedTypesSharedMemCons(synthesisInstance));
		}

		constraints = constraints.append(generalReferenceCons(synthesisInstance.getAllTypes(), synthesisInstance.getTypeAutomaton(), synthesisInstance.getMappings()));

		constraints = constraints.append(outputCons(synthesisInstance));
		return constraints.toString();
	}

	/**
	 * Generating the mutual exclusion constraints for each pair of tools from
	 * modules (excluding abstract modules from the taxonomy) in each state of
	 * moduleAutomaton.
	 * 
	 * @param moduleAutomaton
	 * @param mappings
	 * @return {@link String} representation of constraints
	 */
	public String moduleMutualExclusion(ModuleAutomaton moduleAutomaton, AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();

		for (Pair pair : getToolPairs()) {
			for (State moduleState : moduleAutomaton.getModuleStates()) {
				constraints = constraints.append("-")
						.append(mappings.add(pair.getFirst(), moduleState, WorkflowElement.MODULE)).append(" ");
				constraints = constraints.append("-")
						.append(mappings.add(pair.getSecond(), moduleState, WorkflowElement.MODULE)).append(" 0\n");
			}
		}

		return constraints.toString();
	}

	/**
	 * Generating the mandatory usage constraints of root module @rootModule in each
	 * state of @moduleAutomaton.
	 * 
	 * @param rootModuleID    - represent the ID of the root module in the module
	 *                        taxonomy
	 * @param moduleAutomaton - module automaton
	 * @param mappings
	 * @return String representation of constraints
	 */
	public String moduleMandatoryUsage(AllModules annotatedTools, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings) {
		if(annotatedTools.getModules().isEmpty()) {
			System.err.println("No tools were I/O annotated.");
			return "";
		}
		StringBuilder constraints = new StringBuilder();
		
		for (State moduleState : moduleAutomaton.getModuleStates()) {
			for (AbstractModule tool : annotatedTools.getModules()) {
				constraints = constraints.append(mappings.add(tool, moduleState, WorkflowElement.MODULE))
						.append(" ");
			}
			constraints = constraints.append(" 0\n");
		}

		return constraints.toString();
	}

	/**
	 * Generating the mandatory usage of a submodules in case of the parent module
	 * being used, with respect to the Module Taxonomy. The rule starts from
	 * the @rootModule and it's valid in each state of @moduleAutomaton.
	 * 
	 * @param rootModuleID    - represent the ID of the root module in the module
	 *                        taxonomy
	 * @param moduleAutomaton - module automaton
	 * @param mappings
	 * @return String representation of constraints enforcing taxonomy
	 *         classifications
	 */
	public String moduleEnforceTaxonomyStructure(String rootModuleID, ModuleAutomaton moduleAutomaton,
			AtomMapping mappings) {

		StringBuilder constraints = new StringBuilder();
		for (State moduleState : moduleAutomaton.getModuleStates()) {
			constraints = constraints.append(
					moduleEnforceTaxonomyStructureForState(rootModuleID, mappings, moduleState));
		}
		return constraints.toString();
	}

	/**
	 * Providing the recursive method used in
	 * {@link #moduleEnforceTaxonomyStructure(String, ModuleAutomaton, AtomMapping)
	 * moduleEnforceTaxonomyStructure}.
	 */
	private String moduleEnforceTaxonomyStructureForState(String rootModuleID,
			AtomMapping mappings, State moduleState) {
		AbstractModule currModule = this.get(rootModuleID);
		String superModule_state = mappings.add(currModule, moduleState, WorkflowElement.MODULE).toString();

		StringBuilder constraints = new StringBuilder();
		StringBuilder currConstraint = new StringBuilder("-").append(superModule_state).append(" ");

		List<String> subModules_States = new ArrayList<String>();
		if (!(currModule.getSubModules() == null || currModule.getSubModules().isEmpty())) {
			/*
			 * Ensuring the TOP-DOWN taxonomy tree dependency
			 */
			for (String subModuleID : currModule.getSubModules()) {
				AbstractModule subModule = this.get(subModuleID);
				String subModule_State = mappings.add(subModule, moduleState, WorkflowElement.MODULE).toString();
				currConstraint = currConstraint.append(subModule_State).append(" ");
				subModules_States.add(subModule_State);
				constraints = constraints.append(moduleEnforceTaxonomyStructureForState(subModuleID, mappings, moduleState));
			}
			currConstraint = currConstraint.append("0\n");
			/*
			 * Ensuring the BOTTOM-UP taxonomy tree dependency
			 */
			for (String subModule_State : subModules_States) {
				currConstraint = currConstraint.append("-").append(subModule_State).append(" ")
						.append(superModule_state).append(" 0\n");
			}
			return currConstraint.append(constraints).toString();
		} else {
			return "";
		}
	}

}
