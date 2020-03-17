package nl.uu.cs.ape.sat.core;

import nl.uu.cs.ape.sat.automaton.ModuleAutomaton;
import nl.uu.cs.ape.sat.automaton.TypeAutomaton;
import nl.uu.cs.ape.sat.models.AtomMappings;
import nl.uu.cs.ape.sat.models.logic.constructs.Atom;
import nl.uu.cs.ape.sat.utils.APEDomainSetup;

import java.util.ArrayList;
import java.util.List;

public class ExternalConstraintFactory {

	List<ExternalConstraintBuilder> constraintBuilders;

	AtomMappings atomDictionary;
	APEDomainSetup apeDomain;

	TypeAutomaton typeAutomaton;
	ModuleAutomaton moduleAutomaton;

	public ExternalConstraintFactory(APEDomainSetup apeDomainSetup) {
		this.apeDomain = apeDomainSetup;
		this.constraintBuilders = new ArrayList<>();
	}

	public void AddAtomDictionary(AtomMappings atomMappings) {
		this.atomDictionary = atomMappings;
	}

	public void ResetStates(TypeAutomaton typeAutomaton, ModuleAutomaton moduleAutomaton) {
		this.moduleAutomaton = moduleAutomaton;
		this.typeAutomaton = typeAutomaton;
	}

	public void AddConstraintBuilder(ExternalConstraintBuilder builder){
		this.constraintBuilders.add(builder);
	}

	public StringBuilder ConstructConstraints(){
		StringBuilder result =
			constraintBuilders.stream()
				.map(builder -> builder.Build(apeDomain,moduleAutomaton,typeAutomaton,atomDictionary))
				.reduce(new StringBuilder(), StringBuilder::append);

		return result;
	}

}
