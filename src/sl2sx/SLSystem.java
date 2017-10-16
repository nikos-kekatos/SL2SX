package sl2sx;

import java.util.*;

/**
 *  
 * Class to model a Simulink SubSystem.
 * 
 * @author stefano.minopoli
 */
public class SLSystem {
	
	// (sub)System name
	private String name=new String();
	// Type: (System || SubSystem)
	private String type=new String();
	// Owner
	private int parent;
	// List of the blocks
	private HashMap<String, SLBlock> blocksList = new HashMap<String, SLBlock>();
	// Blocks Number
	private int blockNum=0;
	// List of InPorts
	private ArrayList<String> inPorts = new ArrayList<String>();
	// List of OutPorts
	private ArrayList<String> outPorts = new ArrayList<String>();
	// Number of InPorts
	private int inPortsNum;
	// Number of OutPorts
	private int outPortsNum;
	// List of Variables
	private HashMap<String, Variable> variablesList = new HashMap<String, Variable>();
	// True if the system is flipped.
	private boolean blockMirror=false;
	// Identify MainSystem
	private boolean main = false;
		
	// ------------------------------------>> Public Interface
	
	 /**
     * Default constructor.
     * 
     * @param name
     * 				Simulink (Sub)System Name.
     * @param parent
     * 				Simulink (Sub)System Father.
     */
	public SLSystem(String name, int parent)
	{
		this.name = name;
		this.parent = parent;
		if (parent<0)
		{
			this.type="System";
			this.main = true;
		}
		else
			this.type="SubSystem";
		this.inPortsNum = 0;
		this.outPortsNum = 0;
	}
	
	  /**
     * Set the Name of the SL (Sub)System.
     * 
     * @param name
     * 				Simulink (Sub)System Name.
     */
	public void setName (String name)
	{
		this.name = name;
	}
	
	/**
     * Get the (Sub)System name.
     * @return A String that is the name of the modeled SL (Sub)System.
     */
	public String getName ()
	{
		return this.name;
	}
	
	  /**
     * Set the System Type (System or SubSystem).
     * 
     * @param name
     * 				Simulink (Sub)System Type.
     */
	public void setType (String type)
	{
		this.type = type;
	}
	
	/**
     * Get the (Sub)System Type ("System" or "SubSystem").
     * @return A String that is the Type of the modeled SL (Sub)System.
     */
	public String getType ()
	{
		return this.type;
	}
	
	  /**
     * Set the (Sub)System Parent.
     * 
     * @param parent
     * 				Simulink (Sub)System Parent of *this.
     */
	public void setParent (int parent)
	{
		this.parent = parent;
	}
	
	/**
     * Get the (Sub)System Parent.
     * @return An Integer that is the index of the (Sub)System Parent in the list of (Sub)Systems.
     */
	public int getParent ()
	{
		if (this.parent>-1)
			return this.parent;
		else
			return 0;
	}
	
	/**
     * Check whether *this is the Main System or not.
     * @return True, if *this is the Main System.
     */
	public boolean isMain ()
	{
		return this.main;
	}
	
	  /**
     * Add a Block to the (Sub)System.
     * 
     * @param block
     * 				SLBlock to add to the (sub)System.
     */
	public void addBlock (SLBlock block)
	{
		this.blocksList.put(block.getName(), block);
		// Add also the corresponding variables
		this.addVars(block);
		this.blockNum++;
		if (block.isInPort())
			this.addInPort(block.getName());
		else if (block.isOutPort())
			this.addOutPort(block.getName());
	}
	
	/**
     * Get the list of the Blocks belong to the (Sub)System.
     * @return An HashMap that contains the SL Blocks of the modeled (Sub)System.
     */
	public HashMap<String,SLBlock> getBlocksList()
	{
		return this.blocksList;
	}
	
	
	/**
     * Get a specific (Sub)System Block by name.
     * @param name
     * 				Is the Block Name.
     * @return SLBlock that is the specified SL Block.
     */
	public SLBlock getBlock(String name)
	{
		return this.blocksList.get(name);
	}
	
	/**
     * Get the number of the Blocks in the (Sub)System.
     * @return An Integer that is the number of the (Sub)System Blocks.
     */
	public int getBlockNum()
	{
		return this.blockNum;
	}
	
	/**
     * Get the name of the i-th InPort of the (Sub)System.
     * @param i
     * 			Is the InPort Index.
     * @return A String that is the name of the i-th InPort of the (Sub)System.
     */
	public String getInPort(int i)
	{
		return this.inPorts.get(i);
	}
	
	/**
     * Get the name of the i-th OutPort of the (Sub)System.
     * @param i
     * 			Is the OutPort Index.
     * @return A String that is the name of the i-th OutPort of the (Sub)System.
     */
	public String getOutPort(int i)
	{
		return this.outPorts.get(i);
	}
	
	/**
     * Get the total number of the (Sub)System InPorts.
     * @return An Integer that is the total number of the (Sub)System InPorts.
     */
	public int getInPortNum()
	{
		return this.inPortsNum;
	}
	
	/**
     * Get the total number of the (Sub)System OutPorts.
     * @return An Integer that is the total number of the (Sub)System OutPorts.
     */	
	public int getOutPortNum()
	{
		return this.outPortsNum;
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
     * @return True, if *this model a SubSystem Block with BlockMirror on
     */
	public boolean isBlockMirror()
	{
		return this.blockMirror;
	}
	
	/**
     * Get the list of the Variables involved in the (Sub)System.
     * @return An HashMap that contains the list of the Variables involved in the (Sub)System.
     */
	public HashMap<String, Variable> getVariablesList()
	{
		return this.variablesList;
	}
	
	// Return the variable specified by Name
	/**
     * Get a specified (Sub)System variable by name.
     * @param name
     * 			Is the Variable Name.
     * @return A Variable, corresponds to the var specified by name.
     */
	public Variable getVar(String varName)
	{
		return this.variablesList.get(varName);
	}
	
	  /**
     * Add the Vertex of the graphical line connection of a Source Block.
     * 
     * @param srcBlock
     * 				Name of the SL Source Block.
     * @param srcPort
     * 				Index of the Block OutPort.
     * @param points
     * 				Connection Vertex. Corresponding to the tag <P Points>.
     */
	public void addSrcPoints(String srcBlock, String srcPort, String points)
	{
		if (inPorts.contains(srcBlock))
			variablesList.get(srcBlock).addSrcPoints(points);
		//else
			//System.out.println(srcBlock + "   Out   " + srcPort);
	}
	
  /**
     * Add the Vertex of the graphical line connection of a Destination Block.
     * 
     * @param dstBlock
     * 				Name of the SL Destination Block.
     * @param dstPort
     * 				Index of the Block InPort.
     * @param points
     * 				Connection Vertex. Corresponding to the tag <P Points>.
     * @param inBranch
     * 				Index of the vertex tree.
     */
	public void addDstPoints(String dstBlock, String dstPort, ArrayList<String> points, int inBranch)
	{
		if (outPorts.contains(dstBlock))
			// Add the Points to an OutPort Variable
			variablesList.get(dstBlock).addDstPoints(points, inBranch);
		else
			// Add the Points to a Non-OutPort Variable
			variablesList.get(dstBlock + "In" + dstPort).addDstPoints(points, inBranch);
	}
	
	/**
     * Set a new Mapping derived by reading <Line> tag from Simulink .xml file. 
     * The method also handles InPort and OutPort
     * 
     * @param srcName
     * 				Name of the Source Block.
     * @param srcPort
     * 				Index of the Source OutPort.
     * @param dstName
     * 				Name of the Destination Block.
     * @param dstPort
     * 				Index of the Destination InPort.
     */
	public void setMap(String srcName, String srcPort, String dstName, String dstPort)
	{
		// If an InPort is involved
    	if (inPorts.contains(srcName))
    	{
    		System.out.println("Connection To " + dstName + " Port " + dstPort + " From Input Port " + srcName + "...DONE.");
    		variablesList.get(dstName + "In" + dstPort).setMap(srcName);
    	}
    	// If an OutPort is involved
    	else if (outPorts.contains(dstName))
    	{
    		System.out.println("Connection Form " + srcName + "Out" + srcPort + " To Output Port  " + dstName + "...DONE.");
    		variablesList.get(srcName + "Out" + srcPort).setMap(dstName);	
    	}
    	// If no InPort or OutPort is involved
    	else
    	{
    		System.out.println("Connection To " + dstName + " Port " + dstPort + " From  " + srcName + " Port " + srcPort + "...DONE.");
      		variablesList.get(dstName + "In" + dstPort).setMap(srcName + "Out" + srcPort);
    	}
    }
	
	/**
     * Compute the Graphical Shape of the Link (SpaceEx feature).
     * To be used ONLY AFTER the Simulink source file is processed 
     * and ONLY BEFORE the Variables Normalization.
     *
     */
	public void computeLink()
	{
		Iterator itVars = this.variablesList.entrySet().iterator();
		
		// For each single variable
		while (itVars.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) itVars.next();
		
		    // Get Var and Mapping
		    String varName = (String)entry.getKey();
		    Variable var = (Variable)entry.getValue();
		    String mapName = var.getMap();
		    Variable varMap = variablesList.get(mapName);
		   
		    String link = new String();
		    
		    // If is In or Outport, SKIP
		    if (!var.isInPort() && !var.isOutPort())
		    { 		    	
		    	// From Block to OutPort: just follow the points
		    	if (var.isOutput() && varMap.isOutPort())
		    		link = createLink(var.getXPos(), var.getYPos(),var.getPoints(), var.getPoints());
		    	// If is not From Output Var to (same) Output var
		    	else if (!(var.isOutput() && varMap.isOutput()))
		    	{
		    		// Start from the Map, follow Map points and then var Points
		  			link = createLink(varMap.getXPos(), varMap.getYPos(), varMap.getPoints(), var.getPoints());
		    		// Reverse the link
		    		link = this.backLink(link);
		   		}
		    	// Add the computed Link
		    	this.variablesList.get(varName).setLink(link);
		    }
		}
	}
	
	/**
     * Normalize the variables mapping, to eliminate useless variables (similar to the "clean"
     * operation avaiable in the SpaceEx Editor).
     */
	public void normalizeVars()
	{
		String link = new String();
		
		Iterator itVars = this.variablesList.entrySet().iterator();
		// For each single variable
		while (itVars.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) itVars.next();
		    
		    String varName = (String)entry.getKey();
		    Variable var = (Variable)entry.getValue();

		    // If the varMapping is Mapped to other variable, exchange the Mapping
		    //if (!var.getName().equals(var.getMap()))
		    
		    if (!var.getMap().equals(this.variablesList.get(var.getMap()).getMap()))
		    {
		    	// Put together the original link with the mapping link 
		    	link = var.getLink();
		    	if (!link.isEmpty() && !this.variablesList.get(var.getMap()).getLink().isEmpty())
		    		link = link + ",";
		    	link = link + this.variablesList.get(var.getMap()).getLink();
		    	var.setLink(link);
	
		    	// Exchange tha mapping
		    	var.setMap(this.variablesList.get(var.getMap()).getMap());
		    	this.variablesList.put(varName, var);
		    }
		 }    	
	}		
	
	// ----------------------------->> Private Methods
	
	// Insert a new InPort to the System
	private void addInPort(String inPort)
	{
		inPorts.add(this.inPortsNum, inPort);
		inPortsNum++;
	}
		
	// Insert a new InPort to the System
	private void addOutPort(String outPort)
	{
		outPorts.add(this.outPortsNum, outPort);
		outPortsNum++;
	}
	
	
	// Create (and add to the System) the new variables coming from the specified block 
	private void addVars(SLBlock block)
	{
		boolean hide = false;
		String xPos = new String();
		String yPos = new String();
		String placement = new String();
		
		// If no InPort or OutPort are involved, use the convention "blockname" + ("In" || "Out") + portNum
		if (!(block.isInPort() || block.isOutPort()))
		{
			if (!block.isScope())
				hide = true;
			
			for (int i=0; i<block.getInPortNum(); i++)
			{
				String varName=new String(block.getName() + "In" + (i+1));
				if (block.isBlockMirror())
					xPos = "" + (Integer.parseInt(block.getX1Pos()) + Integer.parseInt(block.getWidth())/2);
				else
					xPos = "" + (Integer.parseInt(block.getX1Pos()) - Integer.parseInt(block.getWidth())/2);
				yPos = "" + ((Integer.parseInt(block.getY1Pos()) - Integer.parseInt(block.getHeight())/2) + ((Integer.parseInt(block.getHeight()) / (block.getInPortNum()+1)) * (i+1)));
				
				Variable var=new Variable(varName, true, false, false, false, block.isScope(), xPos, yPos, hide);
				var.setWestPlacement(!block.isBlockMirror());
				variablesList.put(varName, var);
			}
			for (int i=0; i<block.getOutPortNum(); i++)
			{
				String varName=new String(block.getName() + "Out" + (i+1));
				if (block.isBlockMirror())
					xPos = "" + (Integer.parseInt(block.getX1Pos()) - Integer.parseInt(block.getWidth())/2);
				else
					xPos = "" + (Integer.parseInt(block.getX1Pos()) + Integer.parseInt(block.getWidth())/2);
				yPos = "" + ((Integer.parseInt(block.getY1Pos()) - Integer.parseInt(block.getHeight())/2) + ((Integer.parseInt(block.getHeight()) / (block.getOutPortNum()+1)) * (i+1)));
				
				Variable var=new Variable(varName, false, true, false, false, block.isScope(), xPos, yPos, hide);
				var.setWestPlacement(block.isBlockMirror());
				variablesList.put(varName, var);
			}

			// If Goto Block, add the corresponding variable
			if (block.isGoto())
			{
				String varName=new String("Goto_" + block.getGotoTag());
				if (block.isBlockMirror())
					xPos = "" + (Integer.parseInt(block.getX1Pos()) - Integer.parseInt(block.getWidth())/2);
				else
					xPos = "" + (Integer.parseInt(block.getX1Pos()) + Integer.parseInt(block.getWidth())/2);
				Variable var=new Variable(varName, false, false, false, false, false, xPos, block.getY1Pos(), true);
				var.setWestPlacement(block.isBlockMirror());
				variablesList.put(varName, var);
			}
			// If Goto or From block, add the corresponding map between them
			if (block.isGoto() || block.isFrom())
			{
				if (block.isFrom())
					variablesList.get(block.getName() + "In1").setMap("Goto_" + block.getGotoTag());
				else if (block.isGoto())
					variablesList.get(block.getName() + "Out1").setMap("Goto_" + block.getGotoTag());
			}				
		}
		// If InPort or OutPort involved add just the (In/Out)Port Name
		else
		{
			String varName=new String(block.getName());
			Variable var=new Variable(varName, false, false, block.isInPort(), block.isOutPort(), false, block.getX1Pos(), block.getY1Pos(), false);
			variablesList.put(varName, var);
			// Update the list of InPort
			if (block.isInPort())
				inPorts.add(varName);
			// Update the list of OutPort
			else
				outPorts.add(varName);
		}		
	}
	

	// Create the Link starting from (x1,y1) position and following points1 and the point2
	private String createLink(String x1, String y1, String points1, String points2)
	{
		// Get initial position
		int x = Integer.parseInt(x1);
		int y = Integer.parseInt(y1);
		int incX = 0;
		int incY = 0;
		int start = 0;
		int end = 0;
			
    	// Put Points1 and Point2 together (if possible)
		String link = new String();
    	String points = new String(points1);
    	if (!points.isEmpty() && !points2.isEmpty())
    		points = points + ", ";
    	points = points + points2;

    	// Remove the useless characters
    	points = points.replace(";", ",");
		points = points.replace("][", ", ");
		points = points.replace("[", "");
		points = points.replace("]", "");		
		
		// Loop on each value
		while (end<points.length() && end!=-1)
		{
		   	end = points.indexOf(",", start);
		   	// Get the increment for x
		   	incX = Integer.parseInt(points.substring(start, end));
		   		
		   	start = end + 2;
		   	end = points.indexOf(",", start);
		   	if (end==-1)
		   		end=points.length();
		   	// Get the increment for y
		   	incY = Integer.parseInt(points.substring(start, end));

		   	// Increase x,y
		   	x = x + incX;
			y = y + incY;
			
			// Built link step-by-step
			link = link + x + ".0," + y + ".0";
			if (end!=points.length())
				link = link + ",";
			start = end + 2;
		}		
		return link;
	}
	
	// Return the link in the opposite direction
	private String backLink(String link)
	{
		int start = 0;
		int start2 = 0;
		int end = 0;

		String backLink = new String();
    	
  		// Loop form the end
		while (end<link.length())
		{
			end = link.indexOf(",", start2);
			start2 = end + 1;
			end = link.indexOf(",", start2);
			if (end==-1)
				end=link.length();
	
			// Take the couple and put them at the begin
			backLink = link.substring(start, end) + backLink;
			
			if (end!=link.length())
				backLink = "," + backLink;
			
			start = end + 1;
			start2 = start;
		}
		return backLink;
	}
	
// End of Class
}
    

