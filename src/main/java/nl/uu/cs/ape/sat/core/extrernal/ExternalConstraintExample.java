package nl.uu.cs.ape.sat.core.extrernal;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.State;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AbstractModule;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.Module;
import nl.uu.cs.ape.sat.models.enums.NodeType;
import nl.uu.cs.ape.sat.models.enums.WorkflowElement;
import nl.uu.cs.ape.sat.models.logic.constructs.Atom;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

public class ExternalConstraintExample implements ExternalConstraintBuilder {

	@Override
	public StringBuilder Build(APEDomainSetup domainSetup, ModuleAutomaton moduleAutomaton, TypeAutomaton typeAutomaton, AtomMappings atomDictionary) {

		AbstractModule module = domainSetup.getAllModules().get("add_cpt");

		Module externalModule = new Module("test", "test", "ToolsTaxonomy", null);

		//AbstractModule externalModule = new AbstractModule("test", "test", "ToolsTaxonomy", NodeType.LEAF);

		State state = moduleAutomaton.getModuleStates().get(0);

		//Atom atom = new Atom(module, state, WorkflowElement.MODULE );

		Integer id = atomDictionary.add(module, state, WorkflowElement.MODULE);
		Integer exId = atomDictionary.add(externalModule, state, WorkflowElement.EXTERNAL);

		StringBuilder result = new StringBuilder()
			.append(id)
			.append(" 0\n")
			.append(exId)
			.append(" 0\n");

		return result;
	}
}
