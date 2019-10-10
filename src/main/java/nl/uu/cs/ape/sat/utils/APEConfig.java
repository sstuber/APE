package nl.uu.cs.ape.sat.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.uu.cs.ape.sat.models.Type;
import nl.uu.cs.ape.sat.models.Types;
import nl.uu.cs.ape.sat.models.enums.ConfigEnum;
import nl.uu.cs.ape.sat.models.enums.NodeType;

/**
 * The {@code APEConfig} (singleton) class is used to define the configuration
 * variables required for the proper execution of the library.
 * 
 * @author Vedran Kasalica
 *
 */
public class APEConfig {

	/**
	 * Singleton instance of the class.
	 */
	private static final APEConfig configAPE = new APEConfig();
	/**
	 * Tags used in the ape.config file
	 */
	private final String CONFIGURATION_FILE = "ape.configuration";
	private final String ONTOLOGY_TAG = "ontology_path";
	private final String TOOL_ONTOLOGY_TAG = "toolsTaxonomyRoot";
	private final String DATA_ONTOLOGY_TAG = "dataTaxonomyRoot";
	private final String TYPE_SUBONTOLOGY_TAG = "typesTaxonomyRoot";
	private final String FORMAT_SUBONTOLOGY_TAG = "formatsTaxonomyRoot";
	private final String TOOL_ANNOTATIONS_TAG = "tool_annotations_path";
	private final String CONSTRAINTS_TAG = "constraints_path";
	private final String SHARED_MEMORY_TAG = "shared_memory";
	private final String SOLUTION_PATH_TAG = "solutions_path";
	private final String SOLUTION_MIN_LENGTH_TAG = "solution_min_length";
	private final String SOLUTION_MAX_LENGTH_TAG = "solution_max_length";
	private final String MAX_NO_SOLUTIONS_TAG = "max_solutions";
	private final String EXECUTION_SCRIPTS_FOLDER_TAG = "execution_scripts_folder";
	private final String NO_EXECUTIONS_TAG = "number_of_execution_scripts";
	private final String PROGRAM_INPUTS_TAG = "inputs";
	private final String PROGRAM_OUTPUTS_TAG = "outputs";
	private final String PROGRAM_IO_TYPE_TAG = "type";
	private final String PROGRAM_IO_FORMAT_TAG = "format";
	private final String USE_WORKFLOW_INPUT = "use_workflow_input";
	private final String USE_ALL_GENERATED_DATA = "use_all_generated_data";
	private final String DEBUG_MODE_TAG = "debug_mode";

	/** Path to the taxonomy file */
	private String ontology_path;

	/**
	 * Nodes in the ontology that correspond to the roots of module and data
	 * taxonomies.
	 */
	private String tool_taxonomy_root, data_taxonomy_root;
	/** Root of the data-type taxonomy. */
	private String typeSubntology;

	/** Root of the data-format taxonomy. */
	private String formatSubntology;

	/**
	 * List of nodes in the ontology that correspond to the roots of data type and
	 * data format taxonomies.
	 */
	private List<String> data_taxonomy_subroots = new ArrayList<String>();

	/** Path to the XML file with all tool annotations. */
	private String tool_annotations_path;

	/** Path to the file with all workflow constraints. */
	private String constraints_path;

	/**
	 * {@code true} if the shared memory structure should be used, {@code false} in
	 * case of a restrictive pipeline structure.
	 */
	private Boolean shared_memory;

	/**
	 * Path to the file that will contain all the solutions to the problem in human
	 * readable representation.
	 */
	private String solution_path;

	/**
	 * Min and Max possible length of the solutions (length of the automaton). For
	 * no upper limit, max length should be set to 0.
	 */
	private Integer solution_min_length, solution_max_length;

	/** Max number of solution that the solver will return. */
	private Integer max_no_solutions;

	/**
	 * Path to the folder that will contain all the scripts generated based on the
	 * candidate workflows.
	 */
	private String execution_scripts_folder;

	/**
	 * Number of the workflow scripts that should be generated from candidate
	 * workflows. Default is 0.
	 */
	private Integer no_executions;

	/** Output branching factor (max number of outputs per tool). */
	private Integer max_no_tool_outputs = 3;

	/** Input branching factor (max number of inputs per tool). */
	private Integer max_no_tool_inputs = 3;

	/** Input types of the workflow. */
	private List<Types> program_inputs;
	/** Output types of the workflow. */
	private List<Types> program_outputs;

	/**
	 * Determines the required usage for the data instances that are given as
	 * workflow input:<br>
	 * {@link ConfigEnum#ALL} if all the workflow inputs have to be used,<br>
	 * {@link ConfigEnum#ONE} if one of the workflow inputs should be used or <br>
	 * {@link ConfigEnum#NONE} if none of the workflow inputs has to be used
	 */
	private ConfigEnum use_workflow_input;
	/**
	 * Determines the required usage for the generated data instances:<br>
	 * {@link ConfigEnum#ALL} if all the generated data has to be used,<br>
	 * {@link ConfigEnum#ONE} if one of the data instances that are generated as
	 * output, per tool, has to be used or <br>
	 * {@link ConfigEnum#NONE} if none of the data instances is obligatory to use.
	 */
	private ConfigEnum use_all_generated_data;
	/** {@code true} if debug mode is turned on. */
	private Boolean debug_mode;

	/** Configurations used to read/update the "ape.configuration" file. */
	private Document document;
	private JSONObject jsonObject;

	/**
	 * Initialize the configuration of the project.
	 */
	private APEConfig() {
		File file = new File(CONFIGURATION_FILE);
		String content;
		try {
			content = FileUtils.readFileToString(file, "utf-8");

			// Convert JSON string to JSONObject
			jsonObject = new JSONObject(content);

		} catch (IOException e) {
			System.err.println("Configuration file ./" + CONFIGURATION_FILE
					+ " is not provided at location  or its format is corrupted.");
		}

	}

	/**
	 * Returns the singleton class representing the library configuration.
	 * 
	 * @return
	 */
	public static APEConfig getConfig() {
		return configAPE;
	}

	/**
	 * Setting up the configuration of the library.
	 * 
	 * @return {@code true} if the method successfully set-up the configuration,
	 *         {@code false} otherwise.
	 */
	public boolean defaultConfigSetup() {

		try {
			ontology_path = jsonObject.getString(ONTOLOGY_TAG);
			if (!isValidConfigReadFile(ONTOLOGY_TAG, ontology_path)) {
				return false;
			}
		} catch(JSONException JSONException) {
			System.err.println("Tag '" + ONTOLOGY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}
		try {
			tool_taxonomy_root = jsonObject.getString(TOOL_ONTOLOGY_TAG);
			if (tool_taxonomy_root == null || tool_taxonomy_root == "") {
				System.err.println("Incorrect format of " + TOOL_ONTOLOGY_TAG + " tag in the config file.");
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + TOOL_ONTOLOGY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.data_taxonomy_root = jsonObject.getString(DATA_ONTOLOGY_TAG);
			if (data_taxonomy_root == null || this.data_taxonomy_root == "") {
				System.err.println("Incorrect format of " + DATA_ONTOLOGY_TAG + " tag in the config file.");
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + DATA_ONTOLOGY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}
		try {
			this.typeSubntology = jsonObject.getString(TYPE_SUBONTOLOGY_TAG);
			if (typeSubntology == null || typeSubntology == "") {
				data_taxonomy_subroots.add(typeSubntology);
			}
		} catch (JSONException JSONException) {
			/* Configuration does not have the type sub-ontology */
			typeSubntology = data_taxonomy_root;
		}
		try {
			this.formatSubntology = jsonObject.getString(FORMAT_SUBONTOLOGY_TAG);
			if (formatSubntology == null || formatSubntology == "") {
				data_taxonomy_subroots.add(formatSubntology);
			}
		} catch (JSONException JSONException) {
			/* Configuration does not have the format sub-ontology */
			formatSubntology = data_taxonomy_root;
		}

		try {
			this.tool_annotations_path = jsonObject.getString(TOOL_ANNOTATIONS_TAG);
			if (!isValidConfigReadFile(TOOL_ANNOTATIONS_TAG, this.tool_annotations_path)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err
					.println("Tag '" + TOOL_ANNOTATIONS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.constraints_path = jsonObject.getString(CONSTRAINTS_TAG);
			if (!isValidConfigReadFile(CONSTRAINTS_TAG, this.constraints_path)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + CONSTRAINTS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.shared_memory = jsonObject.getBoolean(SHARED_MEMORY_TAG);
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + SHARED_MEMORY_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.solution_path = jsonObject.getString(SOLUTION_PATH_TAG);
			if (!isValidConfigWriteFile(SOLUTION_PATH_TAG, this.solution_path)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + SOLUTION_PATH_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.solution_min_length = jsonObject.getInt(SOLUTION_MIN_LENGTH_TAG);
			if (this.solution_min_length < 1) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println(
					"Tag '" + SOLUTION_MIN_LENGTH_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.solution_max_length = jsonObject.getInt(SOLUTION_MAX_LENGTH_TAG);
			if (this.solution_max_length < 1) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println(
					"Tag '" + SOLUTION_MAX_LENGTH_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		if (solution_max_length != 0 && solution_max_length < solution_min_length) {
			System.err.println("MAX solution length cannot be smaller than MIN solution length.");
			return false;
		}

		try {
			this.max_no_solutions = jsonObject.getInt(MAX_NO_SOLUTIONS_TAG);
			if (this.max_no_solutions < 0) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err
					.println("Tag '" + MAX_NO_SOLUTIONS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.execution_scripts_folder = jsonObject.getString(EXECUTION_SCRIPTS_FOLDER_TAG);
			if (!isValidConfigWriteFolder(EXECUTION_SCRIPTS_FOLDER_TAG, this.execution_scripts_folder)) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println(
					"Tag '" + EXECUTION_SCRIPTS_FOLDER_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.no_executions = jsonObject.getInt(NO_EXECUTIONS_TAG);
			if (this.no_executions < 0) {
				return false;
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + NO_EXECUTIONS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			JSONArray jsonModuleInputs = jsonObject.getJSONArray(PROGRAM_INPUTS_TAG);
			program_inputs = new ArrayList<Types>();

			for (int i = 0; i < jsonModuleInputs.length(); i++) {
				JSONObject jsonModuleInput = jsonModuleInputs.getJSONObject(i);

				Types input = new Types();
				try {
					String jsonInputType = jsonModuleInput.getString(PROGRAM_IO_TYPE_TAG);
					input.addType(new Type(jsonInputType, jsonInputType, typeSubntology, NodeType.UNKNOWN));
				} catch (JSONException JSONException) {
					/* Configuration input does not have the type */}

				try {
					String jsonInputFormat = jsonModuleInput.getString(PROGRAM_IO_FORMAT_TAG);
					input.addType(new Type(jsonInputFormat, jsonInputFormat, formatSubntology, NodeType.UNKNOWN));
				} catch (JSONException JSONException) {
					/* Configuration input does not have the format */}
				if (!input.getTypes().isEmpty()) {
					program_inputs.add(input);
				}
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + PROGRAM_INPUTS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			JSONArray jsonModuleOutputs = jsonObject.getJSONArray(PROGRAM_OUTPUTS_TAG);
			program_outputs = new ArrayList<Types>();

			for (int i = 0; i < jsonModuleOutputs.length(); i++) {
				JSONObject jsonModuleOutput = jsonModuleOutputs.getJSONObject(i);

				Types output = new Types();
				try {
					String jsonOutputType = jsonModuleOutput.getString(PROGRAM_IO_TYPE_TAG);
					output.addType(new Type(jsonOutputType, jsonOutputType, typeSubntology, NodeType.UNKNOWN));
				} catch (JSONException JSONException) {
					/* Configuration output does not have the type */}
				try {
					String jsonOutputFormat = jsonModuleOutput.getString(PROGRAM_IO_FORMAT_TAG);
					output.addType(new Type(jsonOutputFormat, jsonOutputFormat, typeSubntology, NodeType.UNKNOWN));
				} catch (JSONException JSONException) {
					/* Configuration output does not have the type */}
				if (!output.getTypes().isEmpty()) {
					program_outputs.add(output);
				}
			}
		} catch (JSONException JSONException) {
			System.err
					.println("Tag '" + PROGRAM_OUTPUTS_TAG + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			String tempUseWInput = jsonObject.getString(USE_WORKFLOW_INPUT);
			this.use_workflow_input = isValidConfigEnum(USE_WORKFLOW_INPUT, tempUseWInput);
			if (this.use_workflow_input == null) {
				this.use_workflow_input = ConfigEnum.ALL;
				System.out.println("The default vaule for tag " + USE_WORKFLOW_INPUT + " is: ALL.");
			}
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + USE_WORKFLOW_INPUT + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.use_all_generated_data = jsonObject.getEnum(ConfigEnum.class, USE_ALL_GENERATED_DATA);

			String tempUseGenData = jsonObject.getString(USE_ALL_GENERATED_DATA);
			this.use_all_generated_data = isValidConfigEnum(USE_ALL_GENERATED_DATA, tempUseGenData);
			if (this.use_workflow_input == null) {
				this.use_workflow_input = ConfigEnum.ONE;
				System.out.println("The default vaule for tag " + USE_ALL_GENERATED_DATA + " is: ALL.");
			}
		
		} catch (JSONException JSONException) {
			System.err.println("Tag '" + USE_ALL_GENERATED_DATA + "' in the configuration file is not provided correctly.");
			return false;
		}

		try {
			this.debug_mode = jsonObject.getBoolean(DEBUG_MODE_TAG);
		} catch (JSONException JSONException) {
			System.out.println("Tag '" + DEBUG_MODE_TAG + "' in the configuration file is not provided. Default value is: false.");
			this.debug_mode = false;
		}

		return true;
	}

	/**
	 * @return the {@link #configAPE}
	 */
	public static APEConfig getConfigura() {
		return configAPE;
	}

	/**
	 * @return the {@link #ontology_path}
	 */
	public String getOntology_path() {
		return ontology_path;
	}

	/**
	 * @return the {@link #tool_taxonomy_root}
	 */
	public String getTool_taxonomy_root() {
		return tool_taxonomy_root;
	}

	/**
	 * @return the {@link #data_taxonomy_root}
	 */
	public String getData_taxonomy_root() {
		return data_taxonomy_root;
	}
	
	/**
	 * @return the {@link #typeSubntology}
	 */
	public String getData_taxonomy_type_subroot() {
		return typeSubntology;
	}
	
	/**
	 * @return the {@link #data_taxonomy_subroots}
	 */
	public String getData_taxonomy_format_subroot() {
		return formatSubntology;
	}

	/**
	 * @return the {@link #data_taxonomy_subroots}
	 */
	public List<String> getData_taxonomy_subroots() {
		return data_taxonomy_subroots;
	}

	/**
	 * @return the {@link #tool_annotations_path}
	 */
	public String getTool_annotations_path() {
		return tool_annotations_path;
	}

	/**
	 * @return the {@link #constraints_path}
	 */
	public String getConstraints_path() {
		return constraints_path;
	}

	/**
	 * @return the {@link #shared_memory}
	 */
	public Boolean getShared_memory() {
		return shared_memory;
	}

	/**
	 * @return the {@link #solution_path}
	 */
	public String getSolution_path() {
		return solution_path;
	}

	/**
	 * @return the {@link #solution_min_length}
	 */
	public Integer getSolution_min_length() {
		return solution_min_length;
	}

	/**
	 * @return the {@link #solution_max_length}
	 */
	public Integer getSolution_max_length() {
		return solution_max_length;
	}

	/**
	 * @return the {@link #max_no_solutions}
	 */
	public Integer getMax_no_solutions() {
		return max_no_solutions;
	}

	/**
	 * @return the {@link #execution_scripts_folder}
	 */
	public String getExecution_scripts_folder() {
		return execution_scripts_folder;
	}

	/**
	 * @return the {@link #no_executions}
	 */
	public Integer getNo_executions() {
		return no_executions;
	}

	/**
	 * @return the {@link #max_no_tool_outputs}
	 */
	public Integer getMax_no_tool_outputs() {
		return max_no_tool_outputs;
	}

	/**
	 * @return the {@link #max_no_tool_inputs}
	 */
	public Integer getMax_no_tool_inputs() {
		return max_no_tool_inputs;
	}

	/**
	 * @return the {@link #program_inputs}
	 */
	public List<Types> getProgram_inputs() {
		return program_inputs;
	}

	/**
	 * @return the {@link #program_outputs}
	 */
	public List<Types> getProgram_outputs() {
		return program_outputs;
	}

	/**
	 * @return the {@link #use_workflow_input}
	 */
	public ConfigEnum getUse_workflow_input() {
		return use_workflow_input;
	}

	/**
	 * @return the {@link #use_all_generated_data}
	 */
	public ConfigEnum getUse_all_generated_data() {
		return use_all_generated_data;
	}

	/**
	 * @return the {@link #debug_mode}
	 */
	public Boolean getDebug_mode() {
		return debug_mode;
	}

	/**
	 * @return the {@link #document}
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @return the {@link #configNode}
	 */
	public JSONObject getConfigJsonObj() {
		return jsonObject;
	}
	
	/**
	 * Function that returns the tags that are used in the Constraint file. Function can be used to rename the tags.
	 *  
	 * @param tag
	 * @return
	 */
	public static String getConstraintTags(String tag) {
		switch (tag) {
		case "id":
			return "id";
		case "label":
			return "label";
		case "inputs":
			return "inputs";
		case "outputs":
			return "outputs";
		case "type":
			return "type";
		case "format":
			return "format";
		case "implementation":
			return "implementation";
		case "code":
			return "code";
		default:
			return null;
		}
	}

	/**
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag  - corresponding tag from the config file
	 * @param path - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigWriteFile(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (f.isDirectory()) {
			System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is a directory.");
			return false;
		} else {
			if (!f.getParentFile().isDirectory()) {
				System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is not a valid path.");
				return false;
			} else {
				if (!f.canWrite() && !f.getParentFile().canWrite()) {
					System.err.println(
							"Tag '" + tag + "':\nProvided path: \"" + path + "\" is missing the writing permission.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided path is a valid file path with required
	 * writing permissions. Method is tailored for verifying config file fields.
	 * 
	 * @param tag  - corresponding tag from the config file
	 * @param path - path to the file
	 * @return {@code true} if the file exists or can be created, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigWriteFolder(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isDirectory()) {
			System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is not a directory.");
			return false;
		} else if (!f.canWrite()) {
			System.err
					.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is missing the writing permission.");
			return false;
		}
		return true;
	}

	/**
	 * Method checks whether the provided path corresponds to an existing file with
	 * required reading permissions. Method is tailored for verifying config file
	 * fields.
	 * 
	 * @param tag  - corresponding tag from the config file
	 * @param path - path to the file
	 * @return {@code true} if the file exists and can be read, {@code false}
	 *         otherwise.
	 */
	private static boolean isValidConfigReadFile(String tag, String path) {
		if (path == null || path == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return false;
		}
		File f = new File(path);
		if (!f.isFile()) {
			System.err.println("Tag '" + tag + "':\nProvided path: \"" + path + "\" is not a file.");
			return false;
		} else {
			if (!f.canRead()) {
				System.err.println(
						"Tag '" + tag + "':\nProvided file: \"" + path + "\" is missing the reading permission.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Method checks whether the provided string represent an integer number, and
	 * return the number if it does. Method is tailored for verifying config file
	 * fields.
	 * 
	 * @param tag          - corresponding tag from the config file
	 * @param stringNumber - provided string
	 * @return Integer number represented with the string, {@code null} in case of a
	 *         bad String format.
	 */
	private static Integer isValidConfigInt(String tag, String stringNumber) {
		if (stringNumber == null || stringNumber == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return null;
		} else if (!StringUtils.isNumeric(stringNumber)) {
			System.err.println(
					"Tag '" + tag + "':\nProvided number: \"" + stringNumber + "\" is not in a correct format.");
			return null;
		}

		return Integer.parseInt(stringNumber);
	}

	/**
	 * Method checks whether the provided string represent a boolean value, and
	 * return the boolean if it does. Method is tailored for verifying config file
	 * fields.
	 * 
	 * @param tag        - corresponding tag from the config file
	 * @param stringBool - provided string
	 * @return Boolean value represented with the string, {@code null} in case of a
	 *         bad boolean format.
	 */
	private static Boolean isValidConfigBoolean(String tag, String stringBool) {
		if (stringBool == null || stringBool == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return null;
		} else {
			Boolean boolVal = BooleanUtils.toBooleanObject(stringBool);
			if (boolVal == null) {
				System.err.println("Tag '" + tag + "':\nProvided boolean value: \"" + stringBool
						+ "\" is not in a correct format.");
				return null;
			} else {
				return boolVal;
			}
		}
	}

	/**
	 * Method checks whether the provided string represent an enumeration value
	 * ({@link ConfigEnum}), and return the {@link ConfigEnum} if it does. Method is
	 * tailored for verifying config file fields.
	 *
	 * @param tag        - corresponding tag from the config file
	 * @param stringEnum - provided string
	 * @return Boolean value represented with the string, {@code null} in case of a
	 *         bad boolean format.
	 */
	private static ConfigEnum isValidConfigEnum(String tag, String stringEnum) {
		if (stringEnum == null || stringEnum == "") {
			System.err.println("Tag '" + tag + "' in the configuration file is not provided correctly.");
			return null;
		} else {
			if (stringEnum.toUpperCase().equals("ALL")) {
				return ConfigEnum.ALL;
			} else if (stringEnum.toUpperCase().equals("ONE")) {
				return ConfigEnum.ONE;
			} else if (stringEnum.toUpperCase().equals("NONE")) {
				return ConfigEnum.NONE;
			} else {
				System.err.println("Tag '" + tag + "':\nProvided boolean value: \"" + stringEnum
						+ "\" is not in a correct format.");
			}
		}
		return null;
	}

}