package sl2sx;

import java.util.*;

/**
 * 
 * Class to model a Global Simulink Diagram.
 * 
 * @author stefano.minopoli
 */
public class SXData {
	// List of all (Sub)Systems
	private ArrayList<SLSystem> systemsList = new ArrayList<SLSystem>();
	// Used for the Random Access to the SubSystems
	private ArrayList<String> systemsNameList = new ArrayList<String>();
	// ------------> Change this when new block is supported in a new version
	// <------------------------------
	private String strSupportedBlocks = "System SubSystem Inport Outport Constant Gain Sum Product Integrator Saturate DeadZone Logic";
	// Goto From";
	// ------------------------------------------------------------------------------------------------------
	// List of all the Supported Block in the current version (do not change)
	private ArrayList<String> supportedBlocks = new ArrayList<String>();

	// FileName
	private String fileName = new String();
	// Name of the Main System
	private String mainName = new String();
	// Simulation StartTime
	private String startTime = new String();
	// Simulation StopTime
	private String stopTime = new String();
	// Simulation MaxStep Size (Initialized to the default value)
	private String maxStep = new String("0.01");
	// Simulation Relative Tolerance (Initialized to the default value)
	private String relTol = new String("1.0E-9");
	// Simulation Absolute Tolerance (Initialized to the default value)
	private String absTol = new String("1.0E-12");

	// List of the default blocks
	private HashMap<String, SLBlock> dBlocksList = new HashMap<String, SLBlock>();
	// Parent of Current System
	private int next = -1;
	// Next Insert Position
	private int currSys = -1;
	// Number of Block Par Defaults
	private int dBlockNum = 0;
	// BlockMirror Support
	private boolean mirrorBlockOn = false;
	// Initial States
	private String initStates = new String();
	// Output Variables
	private String outVars = new String("t");

	/**
	 * Default constructor. Just set the Supported Blocks in the Current SL2SX
	 * Version
	 */
	public SXData() {
		// Initialize the List of the Supported Blocks
		setSupportedBlocks();
	}

	/**
	 * Add a new Simulink System to the list of the Systems.
	 * 
	 * @param sys
	 *            Simulink (Sub)System.
	 */
	public void addSys(SLSystem sys) {
		this.next++;
		this.currSys = this.next;
		this.systemsList.add(next, sys);
		this.systemsNameList.add(next, sys.getName());
	}

	/**
	 * Set the Name of the Main System.
	 * 
	 * @param name
	 *            Simulink Main System Name.
	 */
	public void setName(String name) {
		this.mainName = name;
	}

	/**
	 * Set the File Name of the Resulting SpaceEx Model.
	 * 
	 * @param name
	 *            SpaceEx Model FileName.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the Main System Name.
	 * 
	 * @return A String that is the name of main System of the modeled Simulink
	 *         Diagram.
	 */
	public String getName() {
		return this.mainName;
	}

	/**
	 * Set the Current System as the Father of the Current.
	 * 
	 * @param name
	 *            Simulink Block Name.
	 */
	public void goToParent() {
		this.currSys = this.getCurrSys().getParent();
	}

	/**
	 * Get the System by index.
	 * 
	 * @param i
	 *            Is the index of the system.
	 * @return The (Sub)System in the i-th position in the List of the
	 *         (Sub)Systems.
	 */
	public SLSystem getSys(int i) {
		return this.systemsList.get(i);
	}

	/**
	 * Get the index of the System by name.
	 * 
	 * @param name
	 *            Is the name of the system.
	 * @return The Index-List of (Sub)System.
	 */
	public int indexOf(String name) {
		return this.systemsNameList.indexOf(name);
	}

	/**
	 * Get the Total Number of the Systems.
	 * 
	 * @return The number of the (Sub)System in the Simulink Diagram.
	 */
	public int getSysNum() {
		return this.systemsList.size();
	}

	/**
	 * Get the Current System.
	 * 
	 * @return The Current (Sub)System.
	 */
	public SLSystem getCurrSys() {
		return this.systemsList.get(currSys);
	} 

	/**
	 * Get the Index of the Current System.
	 * 
	 * @return The Index of the Current (Sub)System.
	 */
	public int getCurrSysIndex() {
		return this.currSys;
	}

	/**
	 * Add a Block to the List of the Default Block, in order to store the
	 * Default Parameters Values.
	 * 
	 * @param block
	 *            Simulink Block.
	 */
	public void addDBlock(SLBlock block) {
		this.dBlocksList.put(block.getName(), block);
		this.dBlockNum++;
	}

	/**
	 * Get the list of Default Parameters Values.
	 * 
	 * @return An HashMap that contains the list of the Default Parameters for
	 *         each Block Type.
	 */
	public HashMap<String, SLBlock> getDBlocksList() {
		return this.dBlocksList;
	}

	/**
	 * Get the Default Parameters Values for a specified Block Type.
	 * 
	 * @param name
	 *            The Block Type of the block.
	 * @return A Block whose Parameters are set with the Default Values.
	 */
	public SLBlock getDBlock(String name) {
		return this.dBlocksList.get(name);
	}

	/**
	 * Get the size of the list of the Default Blocks Parameters.
	 * 
	 * @return An Integer that is the number of the BlockType.
	 */
	public int getDBlockNum() {
		return this.dBlockNum;
	}

	/**
	 * Get the Simulink FileName.
	 * 
	 * @return A String that is the name of the Simulink Diagram in Input.
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Set the StartTime of the Simulation
	 * 
	 * @param startTime
	 *            The Start Time Simulink Simulation.
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime.substring(0, startTime.indexOf("."));
	}

	/**
	 * Get the Start Time of the Simulink Simulation.
	 * 
	 * @return A String that is the Start Time Simulink Simulation.
	 */
	public String getStartTime() {
		return this.startTime;
	}

	/**
	 * Set the stopTime of the Simulation
	 * 
	 * @param stopTime
	 *            The Stop Time Simulink Simulation.
	 */
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	/**
	 * Get the Stop Time of the Simulink Simulation.
	 * 
	 * @return A String that is the Stop Time Simulink Simulation.
	 */
	public String getStopTime() {
		return this.stopTime;
	}

	
	/**
	 * Set the Simulation Max Step Size.
	 * 
	 * @param maxStep
	 *            The Simulation Max Step Size.
	 */
	public void setMaxStep(String maxStep) {
		if (!maxStep.equals("auto"))
			this.maxStep = maxStep;
	}
	
	/**
	 * Get the Simulation Max Step Size.
	 * 
	 * @return A String that is the Simulation Max Step Size.
	 */
	public String getMaxStep() {
		return this.maxStep;
	}
	
	/**
	 * Set the Simulation Absolute Tolerance.
	 * 
	 * @param maxStep
	 *            The Simulation Absolute Tolerance.
	 */
	public void setAbsTol(String absTol) {
		if (!absTol.equals("auto"))
			this.absTol = absTol;
	}

	/**
	 * Get the Simulation Absolute Tolerance.
	 * 
	 * @return A String that is the Simulation Absolute Tolerance.
	 */
	public String getAbsTol() {
		return this.absTol;
	}
	
	/**
	 * Set the Simulation Relative Tolerance.
	 * 
	 * @param maxStep
	 *            The Simulation Relative Tolerance.
	 */
	public void setRelTol(String relTol) {
		if (!relTol.equals("auto"))
			this.relTol = relTol;
	}

	/**
	 * Get the Simulation Relative Tolerance.
	 * 
	 * @return A String that is the Simulation Relative Tolerance.
	 */
	public String getRelTol() {
		return this.relTol;
	}
	
	/**
	 * Get the list of the Simulink BlockTypes supported by the current version
	 * of SL2SX.
	 * 
	 * @return An Array that contains all the list of the all Supported Simulink
	 *         BlockType.
	 */
	public ArrayList<String> getSupportedBlocks() {
		return this.supportedBlocks;
	}

	/**
	 * Set the MirrorBlock Support to On
	 */
	public void setMirrorBlockOn() {
		this.mirrorBlockOn = true;
	}

	/**
	 * Get the Mirror Block Support Configuration.
	 * 
	 * @return True, if the Mirror Blocks are Supported.
	 */
	public boolean isMirrorBlockOn() {
		return this.mirrorBlockOn;
	}

	/**
	 * Get the Initial States of the global system (used to write the
	 * configuration file).
	 * 
	 * @return a String that represent the set of the initial states.
	 */
	public String getInitStates() {
		return this.initStates;
	}

	/**
	 * Get the Output Variables of the main System.
	 * 
	 * @return a String that represent the set the output variables.
	 */
	public String getOutVars() {
		return this.outVars;
	}

	/**
	 * Add a variable to the list of the SpaceEx Output variables (for the
	 * Confing file).
	 * 
	 * @param varName
	 *            The name of the variable that will be displayed by SpaceEx.
	 */
	public void addOutVar(String varName) {
		// Add to the output variables
		if (!this.outVars.isEmpty())
			this.outVars = this.outVars + ",";
		this.outVars = this.outVars + varName;
	}

	/**
	 * Post Processing of the entire Data Structure. To be used only AFTER the
	 * Simulink Source File is all processed. First Step: Derive the Value of
	 * the Default Blocks. Second Step: Compute the Connections among Blocks.
	 * Third Step: Normalize the Variables.
	 */
	public void postProcess() {
		// Loop on the (Sub)Systems
		for (int i = 0; i < this.getSysNum(); i++) {
			SLSystem sys = this.getSys(i);
			Iterator itSys = sys.getBlocksList().entrySet().iterator();

			// Get Default Blocks Values
			while (itSys.hasNext()) {
				Map.Entry entry = (Map.Entry) itSys.next();
				String key = (String) entry.getKey();
				SLBlock block = (SLBlock) entry.getValue();
				// Set Value of the Default Blocks
				if (block.isDefault() && this.getDBlocksList().get(block.getType()) != null)
					sys.getBlocksList().get(block.getName())
							.copy(this.getDBlocksList().get(block.getType()));
			}
			// ********************************* Prototype
			
			// Compute Links (on not normalized Variables)
			this.getSys(i).computeLink();
			// Normalize the Variables
			this.getSys(i).normalizeVars();
			// Compute Global Output Variables, just for the Main System
			if (sys.isMain())
				computeOutVars(sys);
		}
		// Compute the Initial States
		computeInitStates();
	}

	// --------------------------->> Private Methods

	// Computes Global Output Variables
	private void computeOutVars(SLSystem sys) {
		Iterator itVars = sys.getVariablesList().entrySet().iterator();
		// For each single variable
		while (itVars.hasNext()) {
			Map.Entry entry = (Map.Entry) itVars.next();

			String varName = (String) entry.getKey();
			Variable var = (Variable) entry.getValue();

			if (var.isOutPort())
				this.addOutVar(varName);
			if (var.isScope())
				this.addOutVar(var.getMap());
		}
	}

	// Computes the Initial States
	private void computeInitStates() {
		for (int i = 1; i <= this.getSys(0).getInPortNum(); i++) {
			if (!this.initStates.isEmpty() && i == 1)
				this.initStates = this.initStates + " & ";
			// Set Inport variables to 0
			this.initStates = this.initStates + this.getSys(0).getInPort(i)
					+ "==0";
			if (i < this.getSys(0).getInPortNum())
				this.initStates = this.initStates + " & ";
		}
		// Set Outport variables to 0
		for (int i = 1; i <= this.getSys(0).getOutPortNum(); i++) {
			if (!this.initStates.isEmpty() && i == 1)
				this.initStates = this.initStates + " & ";
			this.initStates = this.initStates + this.getSys(0).getOutPort(i)
					+ "==0";
			if (i < this.getSys(0).getOutPortNum())
				this.initStates = this.initStates + " & ";
		}

		// Loop on the (Sub)Systems
		// for (int i=0; i<this.getSysNum(); i++)
		// {
		// SLSystem sys = this.getSys(i);
		// Iterator itSys = sys.getBlocksList().entrySet().iterator();

		// ????????????
		// Find Variables involved in Integrator BlockType
		// while (itSys.hasNext())
		// {
		// Map.Entry entry = (Map.Entry) itSys.next();
		// String key = (String)entry.getKey();
		// SLBlock block = (SLBlock)entry.getValue();

		// Set out var of integrator to 0
		// if (block.isIntegrator())
		// {
		// if (!this.initStates.isEmpty())
		// this.initStates = this.initStates + " & ";
		// this.initStates = this.initStates + sys.getName() + block.getName() +
		// ".Out1==" + block.getInitialCondition() + " & loc("
		// + sys.getName() + block.getName() + ")==loc01";
		// }
		// }
		// }
	}

	// Initialize the DS that contains the supported blocks
	private void setSupportedBlocks() {
		int start = 0;
		int end = strSupportedBlocks.indexOf(" ");
		String blockType = new String();

		// Just scan the String that contains the supported blocks, and fill an
		// ArrayList
		while (end != -1) {
			blockType = strSupportedBlocks.substring(start, end);
			supportedBlocks.add(blockType);
			start = end + 1;
			end = strSupportedBlocks.indexOf(" ", start);
		}
		blockType = strSupportedBlocks.substring(start,
				strSupportedBlocks.length());
		supportedBlocks.add(blockType);
	}

	// Show the List of the Blocks Supported in the Current Version
	public void printSupported() {
		System.out
				.println("         ----------------------------------------------------------");
		System.out
				.println("           The current version supports the following Blocks Type:");
		System.out.print("           ");
		for (int i = 0; i <= supportedBlocks.size() - 1; i++)
			System.out.print(supportedBlocks.get(i) + " ");
		System.out.println();
		System.out
				.println("         -----------------------------------------------------------");
		System.out.println();
	}
}
