package sl2sx;
import java.util.ArrayList;


/**
 * 
 * Class to model a Simulink Block.
 * 
 * @author stefano.minopoli
 */
public class SLBlock {

	/** Block name */
	private String name=new String();
	// Block Type (i.e. Constant || Gain || ...)
	private String type=new String();
	// Extended Name used to avoid ambiguity with the names
	private String extendedType=new String();
	// Block Ports (same format as the SL)
	private String ports=new String();
	// Block Position (same format as the SL)
	private String position=new String();
	// Block Value (same format as the SL. Usually related to a Constant BlockType)
	private String value=new String();
	// Block Gain (same format as the SL. Usually related to a Gain BlockType)
	private String gain=new String();
	// Block Inputs (same format as the SL. Usually related to a Sum BlockType)
	private String inputs=new String();
	// Block Outputs (same format as the SL. Usually related to a...I don't remember ! :-) )
	private String outputs=new String();
	// Block Criteria (same format as the SL. Usually related to a Switch BlockType)
	private String criteria=new String();
	// Block Threshold (same format as the SL. Usually related to a Switch BlockType)
	private String threshold=new String();
	// Block externalReset (same format as the SL. Usually related to an Integrator BlockType)
	private String externalReset=new String();
	// Integrator Initial Condition (same format as the SL. Related to an Integrator BlockType)
	private String initialCondition=new String();
	// Block LowerValue (same format as the SL. Usually related to a DeadZone BlockType)
	private String lowerValue=new String();
	// Block UpperValue (same format as the SL. Usually related to a DeadZone BlockType)
	private String upperValue=new String();
	// Block LowerLimit (same format as the SL. Usually related to a Saturate BlockType)
	private String lowerLimit=new String();
	// Block UpperValue (same format as the SL. Usually related to a Saturate BlockType)
	private String upperLimit=new String();
		
	// Block GotoTag (same format as the SL. Usually related to a Goto or From BlockType)
	private String gotoTag=new String();	
	// Trigonometry Operator
	private String operator=new String();
	
	// X Left-upper block position. Derived from Positions
	private String x1Pos=new String();
	// Y Left-upper block position. Derived from Positions
	private String y1Pos=new String();
	// Block Width. Derived by Positions
	private String width=new String();
	// Block Height. Derived by Positions
	private String height=new String();
	// Number of the Input Variables. Derived by Ports.
	private int inPortNum=0;
	// Number of the Output Variables. Derived by Ports.
	private int outPortNum=0;
	// Number of additional ports. Derived by Ports.
	private int extraPortNum=0;
	// Identify an InPort Block. Derived by Type.
	private boolean inPort=false;
	// Identify an OutPort Block. Derived by Type.
	private boolean outPort=false;
	// Identify a SubSystem Block. Derived by Type.	
	private boolean subSys=false;
	// Identify a Block with default parameters.
	private boolean defaultB=true;
	// Identify a supported blocks.
	private boolean supported=true;
	
	private boolean constantType=false;
	private boolean gainType=false;
	private boolean sumType=false;
	private boolean productType=false;
	private boolean integratorType=false;
	private boolean switchType=false;
	private boolean fromType=false;
	private boolean gotoType=false;
	private boolean deadZoneType=false;
	private boolean scopeType=false;
	private boolean trigonometryType=false;
	private boolean blockMirror=false;
	private boolean saturateType=false;
	private boolean logicType=false;
	
	
	private int hShift = 150;
	
    /**
     * Default constructor.
     * 
     * @param name
     * 				Simulink Block Name.
     * @param type
     * 				Type of the SL Block.
     * @supportedBlocks
     * 				List of the supported Simulink Blocks.
     * @hShift
     *				Horizontal Shift in the SX Graphical Editor.
     */
	public SLBlock(String name, String type, ArrayList<String> supportedBlocks, int hShift)
	{		
		this.name = name.replace(" ", "_");
		this.name = this.name.replace("-", "_");
			
		this.type = type.replace(" ", "_");
		this.type = this.type.replace("-", "_");
		
		this.extendedType = this.type;
		
		this.inPortNum=1;
		this.outPortNum=1;
		
		this.hShift = hShift;
		
		if (type.equals("SubSystem"))
			this.subSys = true;		
		else if (type.equals("Inport"))
		{
			this.inPort=true;
			this.inPortNum=0;
		}
		else if (type.equals("Outport"))
		{
			this.outPort=true;
			this.outPortNum=0;
		}
		else if (type.equals("Constant"))
		{
			this.inPortNum=0;
			this.constantType=true;
		}
		//else if (type.equals("Gain") || type.equals("DeadZone") 
		//		|| type.equals("Goto") || type.equals("From") 
		//		|| type.equals("Rounding"))
		//{
		//	this.inPortNum=1;
		//	this.outPortNum=1;
		else if (type.equals("Gain"))
				this.gainType=true;
			else if (type.equals("DeadZone"))
				this.deadZoneType=true;
			else if (type.equals("Saturate"))
				this.saturateType=true;
			else if (type.equals("Goto"))
				this.gotoType=true;
			else if (type.equals("From"))
				this.fromType=true;
		//}
		else if (type.equals("Sum"))
				this.sumType = true;
		else if (type.equals("Product"))
			this.productType = true;
		else if (type.equals("Integrator"))
			this.integratorType = true;
		else if (type.equals("Trigonometry"))
			this.trigonometryType = true;
		else if (type.equals("Logic"))
			this.logicType = true;
		
		else if (type.equals("Switch"))
		{
			this.inPortNum=3;
			this.switchType=true;
		}
		else if (type.equals("Scope"))
		{
			this.outPortNum=0;
			this.scopeType=true;
			this.defaultB = false;
		}
		else if (type.equals("Terminator"))
		{
			this.outPortNum=0;
			this.defaultB = false;
		}
		else if (type.equals("FromWorkspace"))
			this.inPortNum=0;
		if (!supportedBlocks.contains(this.type))
			this.supported = false;
	}
	
    /**
     * Set the Name of the SL Block.
     * 
     * @param name
     * 				Simulink Block Name.
     */
	public void setName(String name)
	{
		this.name = name;
	}
	
	
    /**
     * Get the Block Name.
     * @return A String that is the name of the modeled SL Block.
     */
	public String getName()
	{
		return this.name;
	}
	
	 /**
     * Set the Type of the SL Block.
     * 
     * @param type
     * 				Simulink Block Type.
     */
	public void setType(String type)
	{
		this.type = type;
		
		if (type.equals("Constant"))
			this.constantType=true;
		else if (type.equals("Gain"))
			this.gainType=true;
		else if (type.equals("Sum"))	
			this.sumType=true;
		else if (type.equals("Product"))	
			this.productType=true;
		else if (type.equals("Switch"))	
			this.switchType=true;
		else if (type.equals("Integrator"))	
			this.integratorType=true;
		else if (type.equals("DeadZone"))
			this.deadZoneType=true;
		else if (type.equals("Logic"))
			this.logicType=true;
		else if (type.equals("Goto"))
			this.gotoType=true;
		else if (type.equals("From"))
			this.fromType=true;
		else if (type.equals("Trigonometry"))	
			this.trigonometryType=true;
		else if (type.equals("Scope"))	
			this.scopeType=true;
	}
	
	/**
     * Get the Block Type.
     * @return A String that is the Type of the modeled SL Block.
     */
	public String getType()
	{
		return this.type;
	}
	
	 /**
     * Set the Extended Type of the SL Block.
     * 
     * @param extType
     * 				Extended Type.
     */
	public void setExtType(String extType)
	{
		this.extendedType = extType;
	}
		
	/**
     * Get the Block Extended Type.
     * @return A String that is the Extended Type of the modeled SL Block.
     */
	public String getExtType()
	{
		return this.extendedType;
	}
	
	 /**
     * Set the Block Graphical Position and compute the Block Graphical Dimension.
     * 
     * @param position
     * 				Corresponding to the tag <P Position> in the Simulink .xml file.
     */
	public void setPosition(String position)
	{
		this.position = position;
		// Compute the effective Block Position and Dimension
		setPosAndDim();
	}
	
	/**
     * Get the x Left-Upper Block Graphical Position.
     * @return A String that is the x Left-Upper Block Graphical Position.
     */
	public String getX1Pos()
	{
		return this.x1Pos;
	}
	
	/**
     * Get the y Left-Upper Block Graphical Position.
     * @return A String that is the y Left-Upper Block Graphical Position.
     */
	public String getY1Pos()
	{
		return this.y1Pos;
	}

	/**
     * Get the Block Graphical Width.
     * @return A String that is the Graphical Width of the block.
     */
	public String getWidth()
	{
		return this.width;
	}

	/**
     * Get the Block Graphical Height.
     * @return A String that is the Graphical Height of the block.
     */
	public String getHeight()
	{
		return this.height;
	}
	
	/**
     * Set *this as (Graphical) Mirror Block.
     */
	public void setBlockMirror(boolean blockMirror)
	{
		this.blockMirror = blockMirror;
	}
	
	/**
     * Used to check whether BlockMirror is on or not.
     * @return True, if *this model a block with BlockMirror on
     */
	public boolean isBlockMirror()
	{
		return this.blockMirror;
	}
	
	/**
     * Set the Ports associated to the modeled block and compute the number of the Input and Output Ports.
     * 
     * @param ports
     * 				Corresponding to the tag <P Ports> in the Simulink .xml file.
     */
	public void setPorts(String ports)
	{
		this.ports = ports;
		// Set the number of Input and Output Variables
		setPortsNum();		
	}
	
	 /**
     * Set the Value associated to a Constant Block Type.
     * 
     * @param value
     * 				Corresponding to the tag <P Value> in the Simulink .xml file.
     */
	public void setValue(String value)
	{
		this.value = value;
		this.defaultB = false;
	}
	
	/**
     * Get the Value associated to a Constant Block Type.
     * @return A String that is the Value of the modeled Constant Block Type.
     */
	public String getValue()
	{
		return this.value;
	}
	
	 /**
     * Set the Gain associated to a Gain Block Type.
     * 
     * @param gain
     * 				Corresponding to the tag <P Gain> in the Simulink .xml file.
     */
	public void setGain(String gain)
	{
		this.gain = gain;
		this.defaultB = false;
	}
	
	/**
     * Get the Gain associated to a Gain Block Type.
     * @return A String that is the Gain of the modeled Gain Block Type.
     */
	public String getGain()
	{
		return this.gain;
	}

	 /**
     * Set the Input associated Blocks (as Sum, Product, ...)
     * 
     * @param inputs
     * 				Corresponding to the tag <P Inputs> in the Simulink .xml file.
     */
	public void setInputs(String inputs)
	{
		this.inputs = inputs.replace("|", "");
		// Ignore for Logic Block
		if (!this.isLogic())
			this.defaultB = false;
	}
	
	/**
     * Get the Inputs value associated to the Modeled Block.
     * @return A String that is the Inputs value of the modeled Block.
     */
	public String getInputs()
	{
		return this.inputs;
	}

	 /**
     * Set the Output Ports Number associated to the modeled Block.
     * 
     * @param outputs
     * 				Corresponding to the tag <P Output> in the Simulink .xml file.
     */
	public void setOutputs(String outputs)
	{
		this.outputs = outputs;
		this.defaultB = false;
	}
	
	/**
     * Get the Output value associated to the Modeled Block.
     * @return A String that is the Output value of the modeled Block.
     */
	public String getOutputs()
	{
		return this.outputs;
	}
	
	 /**
     * Set the Criteria (>=, >, ~=) associated to a Switch Block Type and check whether 
     * the block is supported or not.
     * 
     * @param criteria
     * 				Corresponding to the tag <P Criteria> in the Simulink .xml file.
     */
	public void setCriteria(String criteria)
	{
		// Take the sign of the inequality of the form "u2 >= Threshold"
		this.criteria=criteria.substring(criteria.indexOf(" ")+1, criteria.indexOf(" ", criteria.indexOf(" ")+1));	
		this.defaultB = false;
		if (criteria.equals("~="))
			this.supported = false;
	}
	
	/**
     * Get the Criteria associated to a Switch Block Type.
     * @return A String that is the Criteria of the Modeled Switch Block.
     */
	public String getCriteria()
	{
		return this.criteria;
	}

	 /**
     * Set the Threshold associated to a Switch Block Type.
     * 
     * @param Threshold
     * 				Corresponding to the tag <P Threshold> in the Simulink .xml file.
     */
	public void setThreshold(String threshold)
	{
		this.threshold = threshold;
		this.defaultB = false;
	}
	
	/**
     * Get the Threshold associated to a Switch Block Type.
     * @return A String that is the Threshold of the Modeled Switch Block.
     */
	public String getThreshold()
	{
		return this.threshold;
	}
	
	 /**
     * Set the External Reset associated to an Integrator Block Type and check whether 
     * the block is supported or not.
     * 
     * @param externalReset
     * 				Corresponding to the tag <P ExternalReset> in the Simulink .xml file.
     */
	public void setExternalReset(String externalReset)
	{
		this.externalReset = externalReset;
		this.defaultB = false;
		// If is present, the block is not supported
		this.supported = false;	
	}

	/**
     * Get the External Reset associated to an Integrator Block Type.
     * @return A String that is the External Reset of the Modeled Integrator Block.
     */
	public String getExternalReset()
	{
		return this.externalReset;
	}

	 /**
     * Set the Initial Condition associated to an Integrator Block Type.
     * 
     * @param initialCondition
     * 				Corresponding to the tag <P InitialCondition> in the Simulink .xml file.
     */
	public void setInitialCondition(String initialCondition)
	{
		this.initialCondition = initialCondition;
		this.defaultB = false;
	}

	/**
     * Get the Initial Condition associated to an Integrator Block Type.
     * @return A String that is the Initial Condition of the Modeled Integrator Block.
     */
	public String getInitialCondition()
	{
		return this.initialCondition;
	}
	
	 /**
     * Set the Lower Value associated to a DeadZone Block Type.
     * 
     * @param lowerValue
     * 				Corresponding to the tag <P LowerValue> in the Simulink .xml file.
     */
	public void setLowerValue(String lowerValue)
	{
		this.lowerValue = lowerValue;
		this.defaultB = false;
	}
	
	/**
     * Get the Lower Value associated to a DeadZone Block Type.
     * @return A String that is the LowerValue of the Modeled DeadZone Block.
     */
	public String getLowerValue()
	{
		return this.lowerValue;
	}
	
	 /**
     * Set the Upper Value associated to a DeadZone Block Type.
     * 
     * @param upperValue
     * 				Corresponding to the tag <P UpperValue> in the Simulink .xml file.
     */
	public void setUpperValue(String upperValue)
	{
		this.upperValue = upperValue;
		this.defaultB = false;
	}

	/**
     * Get the Upper Value associated to a DeadZone Block Type.
     * @return A String that is the UpperValue of the Modeled DeadZone Block.
     */
	public String getUpperValue()
	{
		return this.upperValue;
	}

	
		/**
     * Set the Lower Limit associated to a Saturate Block Type.
     * 
     * @param lowerValue
     * 				Corresponding to the tag <P LowerLimit> in the Simulink .xml file.
     */
	public void setLowerLimit(String lowerLimit)
	{
		this.lowerLimit = lowerLimit;
		this.defaultB = false;
	}
	
	/**
     * Get the Lower Limit associated to a Saturate Block Type.
     * @return A String that is the LowerLimit of the Modeled Saturate Block.
     */
	public String getLowerLimit()
	{
		return this.lowerLimit;
	}
	
	 /**
     * Set the Upper Limit associated to a Saturate Block Type.
     * 
     * @param upperValue
     * 				Corresponding to the tag <P UpperLimit> in the Simulink .xml file.
     */
	public void setUpperLimit(String upperLimit)
	{
		this.upperLimit = upperLimit;
		this.defaultB = false;
	}

	/**
     * Get the Upper Limit associated to a Saturate Block Type.
     * @return A String that is the UpperLimit of the Modeled Saturate Block.
     */
	public String getUpperLimit()
	{
		return this.upperLimit;
	}

	

	/**
     * Set the GotoTag associated to a Goto Block Type, and set the Inport and Outport number to 1.
     * 
     * @param gotoTag
     * 				Corresponding to the tag <P GotoTag> in the Simulink .xml file.
     */
	public void setGotoTag(String gotoTag)
	{
		this.gotoTag = gotoTag;
		this.defaultB = false;
		// Add one Input Variable and one Output Variable
		this.setPorts("[1, 1]");
	}
	
	/**
     * Get the GotoTag Value associated to a Goto Block Type.
     * @return A String that is the GotoTag of the Modeled Goto Block.
     */
	public String getGotoTag()
	{
		return this.gotoTag;
	}
	
	 /**
     * Set the Operator associated to a Trigonometry Block Type (only sin and cos are supported)
     * Or Logic Block Type
     * 
     * @param operator
     * 				Corresponding to the tag <P Operator> in the Simulink .xml file.
     */
	public void setOperator (String operator)
	{
		this.operator = operator;
		if (!operator.equals("cos") && !operator.equals("sin") && !operator.equals("AND"))
			this.supported = false;
		this.defaultB = false;
	}

	/**
     * Get the Operator associated to a Trigonometry Block Type
     * Or Logic Block Type
     * @return A String that is the Operator of the Modeled Trigonometry or Logic Block.
     */
	public String getOperator()
	{
		return this.operator;
	}
	
	/**
     * Used to establish whether the Modeled Block is an Inport Block Type or not.
     * @return True if the Modeled Block is an Inport Block Type.
     */
	public boolean isInPort()
	{
		return this.inPort;
	}
	
	/**
     * Used to establish whether the Modeled Block is an Outport Block Type or not.
     * @return True if the Modeled Block is an Outport Block Type.
     */
	public boolean isOutPort()
	{
		return this.outPort;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Constant Block Type or not.
     * @return True if the Modeled Block is a Constant Block Type.
     */
	public boolean isConstant()
	{
		return this.constantType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Gain Block Type or not.
     * @return True if the Modeled Block is a Gain Block Type.
     */
	public boolean isGain()
	{
		return this.gainType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Sum Block Type or not.
     * @return True if the Modeled Block is a Sum Block Type.
     */
	public boolean isSum()
	{
		return this.sumType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Product Block Type or not.
     * @return True if the Modeled Block is a Product Block Type.
     */
	public boolean isProduct()
	{
		return this.productType;
	}
	
	/**
     * Used to establish whether the Modeled Block is an Integrator Block Type or not.
     * @return True if the Modeled Block is an Integrator Block Type.
     */
	public boolean isIntegrator()
	{
		return this.integratorType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Switch Block Type or not.
     * @return True if the Modeled Block is a Switch Block Type.
     */
	public boolean isSwitch()
	{
		return this.switchType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a From Block Type or not.
     * @return True if the Modeled Block is a From Block Type.
     */
	public boolean isFrom()
	{
		return this.fromType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Goto Block Type or not.
     * @return True if the Modeled Block is a Goto Block Type.
     */
	public boolean isGoto()
	{
		return this.gotoType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a DeadZone Block Type or not.
     * @return True if the Modeled Block is a DeadZone Block Type.
     */
	public boolean isDeadZone()
	{
		return this.deadZoneType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Logic Block Type or not.
     * @return True if the Modeled Block is a Logic Block Type.
     */
	public boolean isLogic()
	{
		return this.logicType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Saturate Block Type or not.
     * @return True if the Modeled Block is a Saturate Block Type.
     */
	public boolean isSaturate()
	{
		return this.saturateType;
	}
	
	/**
     * Used to establish whether the Modeled Block is a Scope Block Type or not.
     * @return True if the Modeled Block is a Scope Block Type.
     */
	public boolean isScope()
	{
		return this.scopeType;
	}

	/**
     * Used to establish whether the Modeled Block is a Trigonometry Block Type or not.
     * @return True if the Modeled Block is a Trigonometry Block Type.
     */
	public boolean isTrigonometry()
	{
		return this.trigonometryType;
	}
	
	/**
     * Used to establish the number of the Inports of the Modeled Block.
     * @return The number of the Block Inports.
     */
	public int getInPortNum()
	{
		return this.inPortNum;
	}
	
	/**
     * Used to establish the number of the Outports of the Modeled Block.
     * @return The number of the Block Outports.
     */
	public int getOutPortNum()
	{
		return this.outPortNum;
	}
	
	/**
     * Used to establish whether the Modeled Block is a SubSystem Block Type or not.
     * @return True if the Modeled Block is a SubSystem Block Type.
     */
	public boolean isSubSys()
	{
		return this.subSys;
	}
	
	/**
     * Used to establish whether the Modeled Block inherits the Defaults Values or not.
     * @return True if the Modeled Block has Default Parameters Value.
     */
	public boolean isDefault()
	{
		return this.defaultB;
	}
	
	/**
     * Used to establish whether the Modeled Block is Supported by SL2SX Current Version or not.
     * @return True if the Modeled Block is currently supported.
     */
	public boolean isSupported()
	{
		return this.supported;
	}
	
	 /**
     * Copy Operator for the Class.
     * 
     * @param block
     * 				SLBlock to Copy.
     */
	public void copy(SLBlock block)
	{
		this.value=block.getValue();
		this.gain=block.getGain();
		this.inputs=block.getInputs();
		this.outputs=block.getOutputs();
		this.criteria=block.getCriteria();
		this.threshold=block.getThreshold();
		this.externalReset=block.getExternalReset();
		this.lowerValue=block.getLowerValue();
		this.upperValue=block.getUpperValue();
		this.lowerLimit=block.getLowerLimit();
		this.upperLimit=block.getUpperLimit();
		this.gotoTag=block.getGotoTag();
		this.operator=block.getOperator();

		//this.x1Pos=block.getX1Pos();
		//this.y1Pos=block.getY1Pos();
		//this.width=block.getWidth();
		//this.height=block.getHeight();
		//this.inPortNum=block.getInPortNum();
		//this.outPortNum=block.getOutPortNum();
		//this.inPort=block.isInPort();
		//this.outPort=block.isOutPort();	
		this.subSys=block.isSubSys();
		this.defaultB=block.isDefault();
		this.supported=block.isSupported();
		
		this.constantType=block.isConstant();
		this.gainType=block.isGain();
		this.sumType=block.isSum();
		this.productType=block.isProduct();
		this.integratorType=block.isIntegrator();
		this.switchType=block.isSwitch();
		this.fromType=block.isFrom();
		this.gotoType=block.isGoto();
		this.deadZoneType=block.isDeadZone();
		this.logicType=block.isLogic();
		this.scopeType=block.isScope();
		this.trigonometryType=block.isTrigonometry();
		this.blockMirror=block.isBlockMirror();
		
	}
	 
	/**
     * Set the Graphical Horizontal Shift for the SX Graphic Editor.
     * 
     * @param hShift
     * 				Defines the Horizontal Shift.
     */
	public void setHorizShift(int hShift)
	{
		this.hShift = hShift;
	}
	
	// ------------------------>> Private Methods

	// Compute the number of the In and the Out Ports 
	private void setPortsNum()
	{
		// If Ports is empty: one Input Var and one Output Var
		if (this.ports.isEmpty())
		{	
			this.inPortNum = 1;
			this.outPortNum = 1;
		}
		else				
		{
			if (!this.ports.equals("[]"))
			{
				if (this.ports.indexOf(",")!=-1)
				{
					// Get the number of the Input and Output vars
					this.inPortNum = Integer.parseInt(this.ports.substring(1, this.ports.indexOf(",")));
					String app=new String(this.ports.substring(this.ports.indexOf(",")+2, this.ports.indexOf("]")));
					this.outPortNum = Integer.parseInt(app.substring(0,1));
					int ind=app.indexOf(",");
					// There are also non-input/non-output ports (maybe Trigger) 
					if (ind!=-1)
						this.extraPortNum = Integer.parseInt(app.substring(ind+2, ind+3));
					this.inPortNum=this.inPortNum+this.extraPortNum;
						//this.outPortNum = Integer.parseInt(this.ports.substring(this.ports.indexOf(",")+2, this.ports.indexOf("]")));
				}
				// If Ports is without value, there are only Input Var
				else
					this.inPortNum = Integer.parseInt(this.ports.substring(1, this.ports.indexOf("]")));
			}
		}
	}
	
	// Compute Position and Dimension for *this (Derived by Positions basic attribute) 
	private void setPosAndDim()
	{
		String x2Pos=new String();
		String y2Pos=new String();
		int x1PosInt;
		int y1PosInt;
		int x2PosInt;
		int y2PosInt;
		
		int start=1;
		// Take the position of the first comma, for LeftUpper X
		int end=position.indexOf(",");
		// Get x left-Upper corner
		String x1Pos = position.substring(start, end);
		x1PosInt = Integer.parseInt(x1Pos);
		
		// After first comma, for LeftUpper Y
		start=end+2;
		end = position.indexOf(",", end+1);
		// Get y left-Upper corner
		String y1Pos = position.substring(start, end);
		y1PosInt = Integer.parseInt(y1Pos);
		
		// After second comma, for RightBottom X
		start=end+2;
		end = position.indexOf(",", end+1);
		// Get x Right-Bottom corner
		x2Pos = position.substring(start, end);
		x2PosInt = Integer.parseInt(x2Pos);
		
		// After third comma, for LeftBottom Y
		start=end+2;
		end = position.indexOf("]", end+1);
		// Get y Right-Bottom corner
		y2Pos = position.substring(start, end);
		y2PosInt = Integer.parseInt(y2Pos);
		
		if (this.isInPort() || this.isOutPort())
		{
			this.x1Pos = "" + (x1PosInt + hShift);
			this.y1Pos = y1Pos;
			this.width = "";
			this.height = "";

		}
		else
		{
			// Compute the Block Center Positions
			this.x1Pos = "" + ((x1PosInt + (x2PosInt - x1PosInt)/2)+hShift);
			this.y1Pos = "" + (y1PosInt + (y2PosInt - y1PosInt)/2);
			// Compute Width and Height
			this.width = "" + (x2PosInt - x1PosInt);
			this.height = "" + (y2PosInt - y1PosInt);
		}
	}
}


