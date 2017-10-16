package sl2sx;

import java.util.*;

import fr.imag.verimag.spaceex_moe.sspaceex.Factory;
import fr.imag.verimag.spaceex_moe.sspaceex.SxBind;
import fr.imag.verimag.spaceex_moe.sspaceex.SxComponentBase;
import fr.imag.verimag.spaceex_moe.sspaceex.SxComponentNetwork;
import fr.imag.verimag.spaceex_moe.sspaceex.SxLocation;
import fr.imag.verimag.spaceex_moe.sspaceex.SxMap;
import fr.imag.verimag.spaceex_moe.sspaceex.SxParam;
import fr.imag.verimag.spaceex_moe.sspaceex.SxSspaceex;
import fr.imag.verimag.spaceex_moe.sspaceex.SxTransition;

import java.io.*;

/**
 *  
 * Class to Build a SpaceEx Model.
 * 
 * @author stefano.minopoli
 */
public class SXBuilder 
{
	private SXData sxData = new SXData();
	private SxSspaceex sx;
	
	 /**
     * Default constructor.
     * 
     * @param sxData
     * 				The Data Structure that contains all the processed Simulink Diagram Informations.
     */
	public SXBuilder (SXData sxData)
	{
		this.sxData = sxData;
		this.sx = Factory.creatSspaceex(new File("SX_" + sxData.getFileName() + ".xml"));
	}
	
	/**
     * Build the SpaceEx Model corresponding to the Simulink Diagram whose Informations are stored
     * in the attribute sxData.
     */
	public void extractSXModel()
	{		
		// Used to avoid to build several Constant and Gain Basic Component
		ArrayList<String> yetCreated = new ArrayList<String>();
		boolean toCreate = false;
		
		// Create BaseComponent to model a Clock
		createClockBaseComponent();
		
	    // Scan the SystemsList
	    for (int i=sxData.getSysNum()-1; i>=0; i--)
		{
			SLSystem sys = sxData.getSys(i);
			
			// Scan the BlocksList
			Iterator itSys = sys.getBlocksList().entrySet().iterator();
			while (itSys.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) itSys.next();
				String key = (String)entry.getKey();
				SLBlock block = (SLBlock)entry.getValue();
				
				// Skip if block is a SubSys or an In/OutPort
				if (block.isSubSys() || block.isInPort() || block.isOutPort())
					toCreate = false;
				// If is a Default or Constant or Gain Block, check whether was yet created
				else if ((block.isDefault() || block.isConstant() || block.isGain()) && !sxData.isMirrorBlockOn())
				{
					// If not yet created
					if (!yetCreated.contains(block.getType()))
					{
						yetCreated.add(block.getType());
						toCreate = true;
					}
				}
				// If is a nonDefault block and also is not a Constant or Gain Type
				// add the SubSystem owner name before block name
				else
				{
					block.setExtType(sys.getName() + block.getName());
					toCreate = true;
				}
				// Write the Basic Component
				if (toCreate)
				{
					// Create the Basic Component
					createBaseComponent(block);
					toCreate = false;
				}
			}				
			// Create the Network Component
			boolean mainSys=false;
			if (i==0)
				mainSys=true;
			createNetworkComponent(sys, mainSys);
		}
	    
		sx.getModDoc().save();
		
		System.out.println("\n File SX_" + sxData.getFileName() + ".xml file...CREATED.\n");
	}
	
	 /**
     * Just create the SpaceEx Base Component that models the General System Clock.
     */
	private void createClockBaseComponent()
	{
		SxComponentBase clock = Factory.creatBaseComponent(sx, "Clock");
		
		// Create the Parameters to model the time
		SxParam t = Factory.creatRealVariable(clock, "t");
		t.setLocal(false);
		t.setControlled(true);
		t.setPlacement("east");

		// Add a Location to the Clock Basic Component
		SxLocation loc1 = Factory.creatLocation(clock);
		loc1.setName("loc01");
		// Add the flow for the time
		loc1.setFlow("t' == 1");
		// ???? Add the invariant to models the Simulation Stop Time		
		//loc1.setInvariant("t<=" + this.sxData.getStopTime());		
	}
	
	 /**
     * Create the SpaceEx Base Component that models a specified Simulink Block.
     * 
     * @param block
     * 				Is the Simulink Block to model.
     */
	private SxComponentBase createBaseComponent(SLBlock block)
	{
		SxComponentBase basic = Factory.creatBaseComponent(sx, block.getExtType());
		
		// Create the Parameters to model the Block Input Variables
		for (int i=1; i<=block.getInPortNum(); i++)
		{
			SxParam in = Factory.creatRealVariable(basic, "In" + i);
			in.setLocal(false);
			in.setControlled(false);
			if (block.isBlockMirror())
				in.setPlacement("east");
			else
				in.setPlacement("west");
		}
		// Create the Parameters to model the Block Output Variables
		for (int i=1; i<=block.getOutPortNum(); i++)
		{
			SxParam out = Factory.creatRealVariable(basic, "Out" + i);
			out.setLocal(false);
			// If the block is an Integrator, the parameter that models the output
			// must be Controlled
			if (block.isIntegrator())
				out.setControlled(true);
			else
				out.setControlled(false);
			if (block.isBlockMirror())
				out.setPlacement("west");
			else
				out.setPlacement("east");
		}
		// Add a Location to a Basic Component
		SxLocation loc1 = Factory.creatLocation(basic);
		loc1.setName("loc01");
		
		// Add the flow for the Integrator
		if (block.getType().equals("Integrator"))
		{
			loc1.setFlow("Out1' == In1");
			if (block.getExternalReset().equals("rising") ||
					block.getExternalReset().equals("falling"))
			{
				// Add another Location to a Basic Component
				SxLocation loc2 = Factory.creatLocation(basic);
				loc2.setName("loc02");
				// Add the invariant for the first location		
				loc1.setInvariant("In2<=0");
				// Add the invariant for the second location		
				loc2.setInvariant("In2>=0");
				// Add a transition from loc1 to loc2
				SxTransition t1 = Factory.creatTransition(basic, loc1, loc2);
				t1.setGuard("In2 == 0");
				// Add a transition fron loc2 to loc1
				SxTransition t2 = Factory.creatTransition(basic, loc2, loc1);
				t2.setGuard("In2 <= 0");

				// Add the corresponding reset
				if (block.getExternalReset().equals("rising"))
					// For rising reset
					t2.setAssignment("Out1:=0");
				else
					// For falling reset
					t1.setAssignment("Out1:=0");
			}
		}	
		else if (block.isConstant())
		{
			SxParam constant = Factory.creatConstant(basic, "K");
			constant.setLocal(false);
			constant.setDynamics("constant");
			// Invariant that models the constant blockType
			loc1.setInvariant("Out1==K");
		}
		else if (block.isGain())
		{
			SxParam gain = Factory.creatConstant(basic, "Gain");	
			gain.setLocal(false);
			gain.setDynamics("constant");
			// Invariant that models the gain blockType
			loc1.setInvariant("Out1==Gain*In1");
		}
		// For Goto or From BlockType
		else if (block.isGoto() ||
				block.isFrom())
			// the invariant just propagate the input to the output
			loc1.setInvariant("Out1==In1");
		// Switch BlockType
		//else if (block.isSwitch() && block.isSupported())
		//{			
			// Add the location to model the second control mode
		//	SxLocation loc2 = Factory.creatLocation(basic);
		//	loc2.setName("loc02");
			// Define the output for each the two of control modes
		//	loc1.setInvariant("Out1==In1 && In2 <= " + block.getThreshold());
		//	loc2.setInvariant("Out1==In3 && In2 >= " + block.getThreshold());
			// Define the transition between the control modes
		//	SxTransition t1 = Factory.creatTransition(basic, loc1, loc2);
		//	t1.setGuard("In2 >= " + block.getThreshold());
		//	SxTransition t2 = Factory.creatTransition(basic, loc2, loc1);
		//	t2.setGuard("In2 <= " + block.getThreshold());
		//	t1.setAsap(true);
		//}
		// Sum blockType
		else if (block.isSum())
		{
			String invariant = new String("Out1==");
			// Take the sequence of the operations (+ or -) and make the invariant
			for (int i=0; i<block.getInputs().length(); i++)
			{
				if (block.getInputs().charAt(i)=='+' && i==0)
					invariant = invariant + "In1";
				else
					invariant = invariant + block.getInputs().charAt(i) + "In" + (i+1);
			}
			
			loc1.setInvariant(invariant);
		}
		// Product blockType
		else if (block.isProduct())
		{
			String invariant = new String("Out1==");

			try
			{
	             int inNum=Integer.parseInt(block.getInputs());
	            
	             for (int i=0; i<inNum; i++)
	             {
	     			if (i==0)
						invariant = invariant + "In1";
					else
						invariant = invariant + "*" + "In" + (i+1);
	             }
	 		}
			catch (NumberFormatException e)
			{
				// Take the sequence of the operations (+ or -) and make the invariant
				for (int i=0; i<block.getInputs().length(); i++)
				{
					if (block.getInputs().charAt(i)=='*' && i==0)
						invariant = invariant + "In1";
					else
						invariant = invariant + block.getInputs().charAt(i) + "In" + (i+1);
			
				}
			}
			loc1.setInvariant(invariant);
		}
		// DeadZone blockType
		else if(block.isDeadZone())
		{
			// Add the location to model the upper UL state
			SxLocation loc2 = Factory.creatLocation(basic);
			loc2.setName("loc02");
			// Add the location to model the lower LL state
			SxLocation loc3 = Factory.creatLocation(basic);
			loc2.setName("loc03");
			
			// Define the output for each the three states
			loc1.setInvariant("Out1==0 && In1 >= " + block.getLowerValue() + " && In1 <= " + block.getUpperValue());			
			loc2.setInvariant("Out1==In1 - " + block.getUpperValue() + " && In1 >= " + block.getUpperValue());
			loc3.setInvariant("Out1==In1 - " + block.getLowerValue() + " && In1 <= " + block.getLowerValue());
			// Define the transition among the states
			SxTransition t1 = Factory.creatTransition(basic, loc1, loc2);
			t1.setGuard("In1>= " + block.getUpperValue());
			SxTransition t2 = Factory.creatTransition(basic, loc2, loc1);
			t2.setGuard("In1 <= " + block.getUpperValue());
			SxTransition t3 = Factory.creatTransition(basic, loc1, loc3);
			t3.setGuard("In1 <= " + block.getLowerValue());
			SxTransition t4 = Factory.creatTransition(basic, loc3, loc1);
			t4.setGuard("In1 >= " + block.getLowerValue());
		}
		// Saturate blockType
		else if(block.isSaturate())
		{
			// Add Constants for Upper and Lower Limit
			SxParam uL = Factory.creatConstant(basic, "uL");	
			uL.setLocal(false);
			uL.setDynamics("constant");
			SxParam lL = Factory.creatConstant(basic, "lL");	
			lL.setLocal(false);
			lL.setDynamics("constant");
			
			// Add the location to model the upper UL state
			SxLocation loc2 = Factory.creatLocation(basic);
			loc2.setName("loc02");
			// Add the location to model the lower LL state
			SxLocation loc3 = Factory.creatLocation(basic);
			loc2.setName("loc03");
			
			// Define the output for each the three states
			loc1.setInvariant("Out1==In1 && In1 >= lL && In1 <= uL");			
			loc2.setInvariant("Out1==lL && In1 <=lL ");
			loc3.setInvariant("Out1==uL && In1 >= uL");
			// Define the transition among the states
			SxTransition t1 = Factory.creatTransition(basic, loc1, loc2);
			t1.setAssignment("Out1'==lL");
			SxTransition t2 = Factory.creatTransition(basic, loc2, loc1);
			t2.setAssignment("Out1'==In1");
			SxTransition t3 = Factory.creatTransition(basic, loc1, loc3);
			t3.setAssignment("Out1'==uL");
			SxTransition t4 = Factory.creatTransition(basic, loc3, loc1);
			t4.setAssignment("Out1'==In1");
		}
	
		else if (block.isTrigonometry())
		{
			if (block.getOperator().equals("sin"))
				loc1.setInvariant("Out1==sin(In1)");
			else if (block.getOperator().equals("cos"))
				loc1.setInvariant("Out1==cos(In1)");
		}
		
		// Logic blockType
		else if(block.isLogic())
		{
			// Add the location to model the False state
			SxLocation loc2 = Factory.creatLocation(basic);
			loc2.setName("loc02");
			
			// Define the output for each of the two states
			loc1.setInvariant("Out1==1");			
			loc2.setInvariant("Out1==0");
			
			if (block.getOperator().equals("AND"))
			{
				int inNum=Integer.parseInt(block.getInputs());
		        // One transition from loc1 to loc2 for each input
				for (int i=0; i<inNum; i++)
		        {
		           	 SxTransition t1 = Factory.creatTransition(basic, loc1, loc2);
		 			 String guard = new String("");
					 guard = "In" + (i+1) + "==0";
					 t1.setGuard(guard);
					 t1.setAsap(true);
		        }
				// A single transition from loc2 to loc1
		    	SxTransition t2 = Factory.creatTransition(basic, loc2, loc1);
		    	String guard = new String("");
		    	for (int i=0; i<inNum; i++)
		        {
		    		if (i<inNum-1)
		    			guard = guard + "In" + (i+1) + "<>0 && ";
		    		else
		    			guard = guard + "In" + (i+1) + "<>0";
				}
		    	t2.setGuard(guard);
			    t2.setAsap(true);
			}
		}
		
		return basic;
	}
	
	
	
	 /**
     * Create the SpaceEx Network Component that models a specified Simulink (Sub)System.
     * 
     * @param sys
     * 				Is the Simulink (Sub)System to model.
     * @param mainSys
     * 				true means that the system is the main system.
     */
	private void createNetworkComponent(SLSystem sys, boolean mainSys)
	{
		ArrayList<String> vars = new ArrayList<String>();
		String placement = new String();
		
		SxComponentNetwork net = Factory.creatNetworkComponent(sx, sys.getName());
		
		// For Each InPorts
		for (int i=0; i<sys.getInPortNum(); i++)
		{
			// Create the corresponding Parameters
			SxParam in = Factory.creatRealVariable(net, sys.getInPort(i));
			in.setLocal(false);
			in.setControlled(false);
			in.setPosition(Integer.parseInt(sys.getVariablesList().get(sys.getInPort(i)).getXPos()), Integer.parseInt(sys.getVariablesList().get(sys.getInPort(i)).getYPos()));
			if (sys.isBlockMirror())
				in.setPlacement("east");
			else
				in.setPlacement("west");
			// Put the Var in an track list to avoid a new creation of the same var
			vars.add(sys.getInPort(i));
		}
		// For Each OutPorts
		for (int i=0; i<sys.getOutPortNum(); i++)
		{
			// Create the corresponding Parameters
			SxParam out = Factory.creatRealVariable(net, sys.getOutPort(i));
			out.setLocal(false);
			out.setControlled(false);
			out.setPosition(Integer.parseInt(sys.getVariablesList().get(sys.getOutPort(i)).getXPos()), Integer.parseInt(sys.getVariablesList().get(sys.getOutPort(i)).getYPos()));
			if (sys.isBlockMirror())
				out.setPlacement("west");
			else
				out.setPlacement("east");
			// Put the Var in an track list to avoid a new creation of the same var
			vars.add(sys.getOutPort(i));
		}
		
		// Scan the Variables List to create the Network Component Paramters 
		Iterator itVars = sys.getVariablesList().entrySet().iterator();
		while (itVars.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) itVars.next();
			String key = (String)entry.getKey();
			Variable var = (Variable)entry.getValue();
			
			// If the variable was not yet defined
			if (!vars.contains(var.getMap()))
			{	
				// InPorts in west side, OutPorts in east
				if (sys.getVariablesList().get(var.getMap()).isInPort())
					placement = "west";
				else 
					placement = "east";
				
				// Create the Parameter
				SxParam par = Factory.creatRealVariable(net, var.getMap());
				// The parameters of the main System are not local
				if(mainSys)
					par.setLocal(false);
				else
					par.setLocal(true);
				par.setControlled(false);
				par.setPosition(Integer.parseInt(sys.getVariablesList().get(var.getMap()).getXPos()), Integer.parseInt(sys.getVariablesList().get(var.getMap()).getYPos()));
				par.setPlacement(placement);
				
				// Put the Var in an track list to avoid a new creation of the same var
				vars.add(var.getMap());
			}
		}		
		// Create and then write the Bind on the outFile
		createBinds(sys, net, mainSys);
	}

		
	 /**
     * Create the SpaceEx bind that models a specified Simulink (Sub)System 
     * in a specified SpaceEx Network.
     * 
     * @param sys
     * 				Is the Simulink (Sub)System to add to the Network Component as bind.
     * @param net
     * 				Is the Network Component where the bind is placed 
     * @param mainSys
     * 				True stay for the NetComponent that models the main System 
     * 
     */
	private void createBinds(SLSystem sys, SxComponentNetwork net, boolean mainSys)
	{		
	
		SxBind bind;
		String compName = new String();
				
		Iterator itSys = sys.getBlocksList().entrySet().iterator();
		while (itSys.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) itSys.next();
			String key = (String)entry.getKey();
			SLBlock block = (SLBlock)entry.getValue();			
			
			// Skip if block is a SubSys or an In/OutPort
			// ??? New for Goto/From New Version
			if (!block.isInPort() && !block.isOutPort())
			{
				if (block.isSubSys())
					compName = block.getName();
				// If is a Default or Constant or Gain Block, check whether was yet created
				else if ((block.isDefault() || block.isConstant() || block.isGain()) && !sxData.isMirrorBlockOn())
					compName = block.getType();
				// If is a nonDefault block and also is not a Constant or Gain Type
				// add the SubSystem owner name before block name
				else
					compName = block.getExtType();				
				// Create the bind with the position, the dimension and the note (if it is the case)
				 //bind = Factory.creatBind(net, this.sx.getComponent(compName), block.getName(), true);
				
				//System.out.println(compName);
				//System.out.println(this.sx.getComponent(compName) + "   " + block.getName());
				
				System.out.println("Bind " + this.sx.getComponent(compName) + " (mapping  " + block.getName() + ") To " + sys.getName() + "...DONE.");
				
				bind = Factory.creatBind(net, this.sx.getComponent(compName), block.getName());
				bind.setPosition(Integer.parseInt(block.getX1Pos()), Integer.parseInt(block.getY1Pos()));
				bind.setDimension(Integer.parseInt(block.getWidth()), Integer.parseInt(block.getHeight()));			
				
				// Add note-info on the Bind
				if (!block.isSupported())					
					bind.setNote("Warning: not supported Simulink Block Type.\n\n" +
								 "The current SL2SX version can not handle: " + block.getType() + " SL Block Type");
				else
					bind.setNote("This SX Component is used to model <<" + block.getType() + ">> SL Block Type");				
				
				// Once created the bind, create the mapping among the bind parameters
				createMapping(sys, block, bind);
			}
		}
		// Add the Global Clock to the Main System
		if (mainSys)
		{
			bind = Factory.creatBind(net, this.sx.getComponent("Clock"), "Clock", true);
			bind.setPosition(150, 10);
			bind.setDimension(10, 10);
		}
	}
	
	 /**
     * Create a SpaceEx mapping among variables, and add the graphical shape of the link.
     * 
     * @param sys
     * 				Is the Simulink (Sub)System where the mapping is defined.
     * @param block
     * 				Is the Simulink Block related to the involved variables.
     * @param bind
     * 				Is the SpaceEx bind where the mapping is created.
     * 
     */
	private void createMapping(SLSystem sys, SLBlock block, SxBind bind)
	{
		String key = new String();
		String key1 = new String();
		String map = new String();
		
		int inPortsNum = 0;
		int outPortsNum = 0;
		
		// If the block is a SubSystem, add the Mapping for the In/Out Ports
		if (block.isSubSys())
		{
			SLSystem actSys = sxData.getSys(sxData.indexOf(block.getName()));
			inPortsNum = actSys.getInPortNum();
			outPortsNum = actSys.getOutPortNum();
			
			// InPorts Mapping
			for (int i=0; i<inPortsNum; i++)
			{
				key = block.getName() + "In" + (i+1);
				key1 = actSys.getInPort(i);
				map = sys.getVariablesList().get(key).getMap();
				
				// Add the mapping
				SxMap m = bind.getMap(key1).setIsASymbolMap(true).setLinkValid(map);
				// Add the link, if it is present
				if (!sys.getVariablesList().get(key).getLink().equals(""))
					m.setMapLink(sys.getVariablesList().get(key).getLink());
				
			}
			// OutPorts Mapping
			for (int i=0; i<outPortsNum; i++)
			{
				key = block.getName() + "Out" + (i+1);
				key1 = actSys.getOutPort(i);
				map = sys.getVariablesList().get(key).getMap();
				
				// Add the mapping
				SxMap m = bind.getMap(key1).setIsASymbolMap(true).setLinkValid(map);
				// Add the link, if it is present				
				if (!sys.getVariablesList().get(key).getLink().equals(""))
					m.setMapLink(sys.getVariablesList().get(key).getLink());
			}
		}
		// For Input or Output variables
		else
		{
			inPortsNum = block.getInPortNum();
			outPortsNum = block.getOutPortNum();
				
			// Input Variables Mapping
			for (int i=0; i<inPortsNum; i++)
			{
				key = block.getName() + "In" + (i+1);
				map = sys.getVariablesList().get(key).getMap();
				
				// Add the mapping
				SxMap m = bind.getMap("In" + (i+1)).setIsASymbolMap(true).setLinkValid(map);
				// Add the link, if it is present
				if (!sys.getVariablesList().get(key).getLink().equals(""))
					m.setMapLink(sys.getVariablesList().get(key).getLink());
				// ?????? Future: wireless signal
				//if (block.isFrom() || block.isGoto())
					//m.setMapLink.setHide();
			}
			// Output Variables Mapping
			for (int i=0; i<outPortsNum; i++)
			{
				key = block.getName() + "Out" + (i+1);
				map = sys.getVariablesList().get(key).getMap();
				
				// Add the mapping
				SxMap m = bind.getMap("Out" + (i+1)).setIsASymbolMap(true).setLinkValid(map);
				// Add the link, if it is present
				if (!sys.getVariablesList().get(key).getLink().equals(""))
					m.setMapLink(sys.getVariablesList().get(key).getLink());
				// ?????? Future: wireless signal
				//if (block.isFrom() || block.isGoto())
					//m.setMapLink.setHide();
			}
			
			// Add the mapping to VALUES (for Constant, Gain and Saturate)
			SxMap mVal;			
			if (block.isConstant())
				mVal = bind.getMap("K").setIsASymbolMap(true).setLinkValid(block.getValue());
			else if (block.isGain())
				mVal = bind.getMap("Gain").setIsASymbolMap(true).setLinkValid(block.getGain());
			else if (block.isSaturate())
			{
				mVal = bind.getMap("uL").setIsASymbolMap(true).setLinkValid(block.getUpperLimit());
				mVal = bind.getMap("lL").setIsASymbolMap(true).setLinkValid(block.getLowerLimit());
			}
		}		
	}
	
	/**
     * Build the Corresponding SpaceEx Model Configuration
     */
	public void extractSXModelConfiguration()
	{
		 try 
		 {
			 // False in order to append
			 PrintWriter Output =
					 		new PrintWriter(new BufferedWriter(new FileWriter("SX_" + sxData.getFileName() + ".cfg", false)));
			 Output.println("system = " + sxData.getName());
		//	 Output.println("initially = \"t==" + sxData.getStartTime() + " & " + sxData.getInitStates() + "\"");
			 Output.println("initially = \"t==" + sxData.getStartTime() + "\"");
			 

		//	 Output.println("initially = \"ControllerOut1==0 & PlantOut1==0 & PlantOut2==0 & t==0\"");
			 Output.println("forbidden = \"\"");
			 Output.println("scenario = simu");
			 Output.println("directions = box");
			 Output.println("set-aggregation = \"none\"");
			 //Output.println("sampling-time = " + sxData.getMaxStep());
			 Output.println("sampling-time = 1");
			 //Output.println("simu-init-sampling-points = 1");
			 // New
			 Output.println("flowpipe-tolerance=1");
			 Output.println("flowpipe-tolerance-rel=1");
			 Output.println("time-horizon = " + this.sxData.getStopTime());
			 Output.println("simu-init-sampling-points = 0");
//			 Output.println("output-variables = \"t, PlantOut1\"");
			 Output.println("output-variables = \"t\"");
			 Output.println("output-format = GEN");
			 Output.println("verbosity = m");
			 Output.println("output-error = 0");
			 Output.println("rel-err = 1.0E-12");
			 Output.println("abs-err = 1.0E-15");
			// End			 
			 Output.println("iter-max = -1");
//			 Output.println("output-variables = \"" + sxData.getOutVars() + "\"");
			 //Output.println("output-format = INTV");
			 //Output.println("verbosity = D4");
			 //Output.println("output-error = 0.000001");	
			 //Output.println("rel-err = " + sxData.getRelTol());
			 //Output.println("abs-err = " + sxData.getAbsTol());
			 //Output.println("rel-err = 1.0E-9");
			 //Output.println("abs-err = 1.0E-12");

			 Output.close();
		 }
		 catch (IOException e) 
		 {
			 System.out.println("Error: " + e);
			 System.exit(1);
		 }		
	}
}
