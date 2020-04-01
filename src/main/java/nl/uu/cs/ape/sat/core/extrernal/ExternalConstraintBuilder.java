package nl.uu.cs.ape.sat.core.extrernal;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

public interface ExternalConstraintBuilder {

	public StringBuilder Build(
		APEDomainSetup domainSetup,
		ModuleAutomaton moduleAutomaton,
		TypeAutomaton typeAutomaton,
		AtomMappings atomDictionary,
		int maxBound
	);
}
