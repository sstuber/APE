package nl.uu.cs.ape.sat.models.enums;

/**
 * Defines the values describing the states in the workflow.
 * <br>
 * <br>
 * values:
 * <br>
 * {@code TOOL, MEMORY_TYPE, USED_TYPE, MEM_TYPE_REFERENCE}
 */
public enum WorkflowElement {

	/**
	 * Depicts usage of a tool/module.
	 */
	MODULE,
	/**
	 * Depicts the creation of a new type instance to the memory.
	 */
	MEMORY_TYPE,
	/**
	 * Depicts the usage of an already created type instance. Usually as an input for a tool.
	 */
	USED_TYPE,
	/**
	 * Depicts the usage of an already created type instance, as an input for a tool. It references the created data type.
	 */
	MEM_TYPE_REFERENCE,

	EXTERNAL;

	public static String getStringShorcut(WorkflowElement elem, Integer blockNumber, int stateNumber) {

		if (elem == MODULE) {
			return "Tool" + stateNumber;
		} else if (elem == MEMORY_TYPE) {
			return "MemT" + blockNumber + "." + stateNumber;
		} else if (elem == USED_TYPE) {
			return "UsedT" + blockNumber + "." + stateNumber;
		} else if (elem == EXTERNAL)
			return "Ext" + stateNumber;
		return "NaN";
	}
}
