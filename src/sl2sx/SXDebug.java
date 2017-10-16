package sl2sx;
import java.util.Iterator;
import java.util.Map;

/** 
 * 
 * Class to Handle Debug Outputs.
 * 
 * @author stefano.minopoli
 */
public class SXDebug {

	private SXData sxData = new SXData();
	
	
	 /**
     * Default constructor.
     * 
     * @param sxData
     * 				Is the Data Structure that contains all the (processed) Simulink Diagram Informations.
     */
	public SXDebug (SXData sxData)
	{
		this.sxData = sxData;
	}
	
	 /**
     * Print info about the diagram (with different verbose level): SystemName, InPorts, OutPorts, Parent, 
     * List of Blocks and List of Variables.
     * 
     * @param system
     * 				If True, print high-level info.
     * @param blocks
     * 				If True, print info about blocks.
     * @param variables
     * 				If True, print info about variables.
     * 
     */
	public void printModel(boolean systems, boolean blocks, boolean variables)
	{
		if (systems) 
			System.out.println("***************** Systems List: Begin *************************");

		for (int i=0; i<sxData.getSysNum(); i++)
		{
			SLSystem sys = sxData.getSys(i);
			
			if (systems || blocks || variables)				 
				System.out.println("SystemName: " + sys.getName());
			
			if (systems) 
			{
				System.out.println("   InPorts ");
				for (int j=0; j<sys.getInPortNum(); j++)
					System.out.println(sys.getInPort(j));
				System.out.println("   OutPorts ");
				for (int j=0; j<sys.getOutPortNum(); j++)
					System.out.println(sys.getOutPort(j));
				System.out.println("   Parent: " + sxData.getSys(sys.getParent()).getName());
				System.out.println();
			}
		
			if (blocks)
				printBlocks(sys);
			if (variables)
				printVariables(sys);
		}
		if (systems) 
			System.out.println("******************** Systems List: END ***********************");		
	}
	 
	/**
     * Print info about blocks belong to a specified (Sub)System.
     * 
     * @param sys
     * 				Specify the (Sub)System.
     */
	public void printBlocks(SLSystem sys)
	{
		System.out.println("****" + sys.getName()  + " Blocks List: BEGIN ****");
			
		Iterator itSys = sys.getBlocksList().entrySet().iterator();
		while (itSys.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) itSys.next();
			String key = (String)entry.getKey();
			SLBlock block = (SLBlock)entry.getValue();
			System.out.println("BlockName: " + block.getName());
			System.out.println("BlockType: " + block.getType());
			System.out.println("X1Pos + Y1Pos: " + block.getX1Pos() + " " + block.getY1Pos());
			System.out.println("Width + Height: " + block.getWidth() + " " + block.getHeight());
			System.out.println("Value: " + block.getValue());
			System.out.println("Gain: " + block.getGain());
			System.out.println("Inputs: " + block.getInputs());
			System.out.println("Outputs: " + block.getOutputs());
			System.out.println("Criteria: " + block.getCriteria());
			System.out.println("Threshold: " + block.getThreshold());
			System.out.println("ExternalReset: " + block.getExternalReset());
			System.out.println("LowerValue: " + block.getLowerValue());
			System.out.println("UpperValue: " + block.getUpperValue());
			System.out.println("GotoTag: " + block.getGotoTag());
			System.out.println("IsInport: " + block.isInPort());
			System.out.println("IsOutport: " + block.isOutPort());
			System.out.println("Inport Number: " + block.getInPortNum());
			System.out.println("Outport Number: " + block.getOutPortNum());
			System.out.println("IsSubsys: " + block.isSubSys());
			System.out.println("IsDefault: " + block.isDefault());
			System.out.println("IsSupported: " + block.isSupported());				
			System.out.println(); 
		}
		System.out.println("****" + sys.getName()  + " Blocks List: END ****");
	}
	
	/**
     * Print info about variables belong to a specified (Sub)System.
     * 
     * @param sys
     * 				Specify the (Sub)System.
     */
	public void printVariables(SLSystem sys)
	{	
		System.out.println("****" + sys.getName()  + " Variables List: BEGIN ****");
		
		Iterator itVars = sys.getVariablesList().entrySet().iterator();
		while (itVars.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) itVars.next();
			String key = (String)entry.getKey();
			Variable var = (Variable)entry.getValue();
			System.out.println("Var Name: " + var.getName());
			System.out.println("Mapped to: " + var.getMap());
			System.out.println("Position: " + var.getXPos() + ", " + var.getYPos());
			System.out.println("Points: " + var.getPoints());
			System.out.println("Link: " + var.getLink());
			System.out.println("West Placement: " + var.isWestPlacement());
			System.out.println("IsOutPort: " + var.isOutPort());
			System.out.println();
		}
		System.out.println("******" + sys.getName()  + " Variables List: END ******");
	}
}
