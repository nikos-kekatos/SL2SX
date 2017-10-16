package sl2sx;
import java.util.ArrayList;

/**
 * 
 * Class to model a Simulink/SpaceEx Variable.
 * 
 * @author stefano.minopoli
 */
public class Variable {
	
	// Variable Name
	private String name=new String();
	// Nome of the variable linked to *this
	private String varMap=new String();
	// Points: directly from SL model
	private String points=new String();
	// Effective connecting path between *this and the mapped variable
	private String link=new String();
	// X Position
	private String xPos=new String();
	// Y Position
	private String yPos=new String();	
	// For controlled variable
	private boolean controlled=false;
	// Identify an Input Variable
	private boolean input=false;
	// Identify an Output Variable
	private boolean output=false;
	// Identify an OutPort variable
	private boolean outPort=false;
	// Identify an InPort variable
	private boolean inPort=false;
	// Identify visible/not visible (in the Editor) Variable
	private boolean hide=false;
	// Identify a Scope variable
	private boolean scope=false;
	// Side Position
	private boolean westPlacement=false;
	

	 /**
     * Default constructor.
     * 
     * @param varName
     * 				The Name of the Variable.
     * 				Rules: if the variable models a Simulink Block Input Variable, the name is
     * 				obtained by appending the label "In" followed by the variable index to the
     * 				block name.	If the variable models a Simulink Block Output Variable, the name is
     * 				obtained by appending the label "Out" followed by the variable index to the
     * 				block name.
     * @param input
     * 				If True, the variable models an Input Block Variable.
     * @param output
     * 				If True, the variable models an Output Block Variable.
     * @param inport
     * 				If True, the variable models an Inport Block Type.
     * @param outport
     * 				If True, the variable models an Outport Block Type.
     * @param xPos
     * 				Specifies the x-upper left vertex of the graphical block.
     * @param yPos
     * 				Specifies the y-upper left vertex of the graphical block.
     * @param hide
     * 				If True, the variable will be not shown in the Grapphical Model Editor
     */
	public Variable(String varName, boolean input, boolean output, boolean inPort, boolean outPort, boolean scope, String xPos, String yPos, boolean hide)
	{
		this.name = varName;
		this.varMap = varName;
		this.input = input;
		this.output = output;
		this.outPort = outPort;
		this.scope = scope;
		this.inPort = inPort;
		this.xPos = xPos;
		this.yPos = yPos;
		this.hide = hide;		
	} 
	
	 /**
     * Set the Variable Name.
     * 
     * @param varName
     * 				The Variable Name.
     */
	public void setName(String varName)
	{
		this.name = varName;
		this.varMap = varName;
	}
	
	
	/**
     * Get the Variable name.
     * @return A String that is the name of the modeled Variable.
     */
	public String getName() 
	{
		return this.name;
	}
	
	 /**
     * Set the x Position of the variable.
     * 
     * @param x
     * 				Horizontal Position of the Variable.
     */
	public void setXPos(String x)
	{
		this.xPos = x;
	}
	
	 /**
     * Set the x Position of the variable.
     * 
     * @param x
     * 				Vertical Position of the Variable.
     */	public void setYPos(String y)
	{
		this.yPos = y;
	}
	
 	/**
     * Get the Horizontal Graphical Position of the Variable.
     * @return A String that is Horizontal Graphical Position of the Variable.
     */
	public String getXPos()
	{
		return this.xPos;
	}
	
	/**
     * Get the Vertical Graphical Position of the Variable.
     * @return A String that is Vertical Graphical Position of the Variable.
     */	public String getYPos()
	{
		return this.yPos;
	}
	
    /**
      * Set *this as Input Variable
      */	public void setInput()
 	{
 		this.input = true;
 	}
     
 	/**
     * Used to check whether *this models an Input Variable.
     * @return True, if *this models an Input Variable.
     */
	public boolean isInput()
	{
		return this.input;
	}
	
	/**
     * Set *this as Output Variable
     */	public void setOutput()
	{
		this.output = true;
	}
	
 	/**
     * Used to check whether *this models an Output Variable.
     * @return True, if *this models an Output Variable.
     */
	public boolean isOutput()
	{
		return this.output;
	}
	
	/**
     * Set *this as Outport Block Variable
     */	public void setOutport()
	{
		this.outPort = true;
	}
	
 	/**
     * Used to check whether *this models an OutPort Block.
     * @return True, if *this models an OutPort.
     */
	public boolean isOutPort()
	{
		return this.outPort;
	}
	
	/**
     * Set *this as Inport Block Variable
     */	public void setInport()
	{
		this.inPort = true;
	}
	
	/**
     * Used to check whether *this models an InPort Block.
     * @return True, if *this models an InPort.
     */
	public boolean isInPort()
	{
		return this.inPort;
	}
	
	/**
     * Used to check whether *this models a Scope Variable.
     * @return True, if *this models a Scope variable.
     */
	public boolean isScope()
	{
		return this.scope;
	}
	
	/**
     * Set *this as Hide Variable
     */	public void setHide()
	{
		this.hide = true;
	}
	
	/**
     * Used to check whether *this models an Hide variable.
     * @return True, if *this models an Hide Variable.
     */
	public boolean isHide()
	{
		return this.hide;
	}
	
	 /**
     * Map *this with the specified variable.
     * 
     * @param varMap
     * 				The mapping variable.
     */
	public void setMap(String varMap)
	{
		this.varMap=varMap;
	}
	
	/**
     * Get the variable mapped to the modeled variable.
     * @return The VarMap associated to *this.
     */
	public String getMap()
	{
		return this.varMap;
	}

	 /**
     * Add graphical line vertex (if *this models a Simulink source variable).
     * 
     * @param points
     * 				Line Graphical Vertex.
     */
	public void addSrcPoints(String points)
	{
			this.points = points;
	}
	
	 /**
     * Add graphical line vertex (if *this models a Simulink destination variable).
     * 
     * @param points
     * 				Line Graphical Vertex.
     */
	public void addDstPoints(ArrayList<String> points, int length)
	{
		for (int i=0; i<length; i++)
			this.points = this.points + points.get(i);
	}
	
	 /**
     * Set the graphical shape of the connection lines.
     * 
     * @param link
     * 				Line Graphical Shape, defined by vertex.
     */
	public void setLink(String link)
	{
		this.link = link;

	}
	
	/**
     * Get the line shape of the mapping.
     * @return A String that contains the vertex that define the graphical line shape of the connection.
     */
	public String getLink()
	{
		return this.link;
	}
	
	/**
     * Get the shape of the line-connection expressed in the original Simulink Format.
     * @return A String that contains the points as defined in the Simulink model.
     */
	public String getPoints()
	{
		return this.points;
	}
	
	 /**
     * Set *this as controlled/uncontrolled variable (by the SpaceEx meaning). 
     * 
     * @param controlled
     * 					If True, *this is a controlled SpaceEx parameter.
     * 				
     */
	public void setContolled(boolean controlled)
	{
		this.controlled = controlled;
	}
	
	/**
     * Used to check whether *this models a Controlled parameter or not.
     * @return True, if *this models a Controlled Parameter.
     */
	public boolean isContolled()
	{
		return this.controlled;
	}
	
	 /**
     * Set the placement to West or East.
     * 
     * @param westPlacement
     * 				Is the desidered placement.
     */
	public void setWestPlacement(boolean westPlacement)
	{
			this.westPlacement = westPlacement;
	}
	
	/**
     * Check if *this is placed at West or not.
     * @return True, if *this is West-Placed.
     */
	public boolean isWestPlacement()
	{
		return this.westPlacement;
	}
		
// End of Class	
}
