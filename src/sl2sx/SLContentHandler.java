package sl2sx;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import java.util.*;


/**
 * 
 * Class to Parse Simulink .xml File.
 * 
 * @author stefano.minopoli
 */
public class SLContentHandler implements ContentHandler {
	
	// 	>> -------------------------------- Definition of the Parameters	
	
	// Debug Verbosity
	boolean printSystemsInfo = true;
	boolean printBlocksInfo = true;
	boolean printVarsInfo = true;
	// Define the Horizontal Shift for the SX graphical representation
	int hShift = 150;
	
	// -----------------------------------------------------------------
	
	// Create the Data Structure that will contain the data from the SL diagram
	SXData sxData = new SXData();
	SLSystem sys;
	SLBlock block;
	
	boolean mainSys = true;
	boolean toInsertBlock = true;
	
	boolean inBlock = false;	
	boolean toGetStartTime = false;
	boolean toGetStopTime = false;
	boolean toGetMaxStep = false;
	boolean toGetRelTol = false;
	boolean toGetAbsTol = false;
	boolean toGetDefault = false;
	boolean toGetPorts = false;
	boolean toGetPosition = false;
	boolean toGetBlockMirror = false;
	boolean toGetValue = false;
	boolean toGetGain = false;
	boolean toGetInputs = false;
	boolean toGetOutputs = false;
	boolean toGetCriteria = false;
	boolean toGetThreshold = false;
	boolean toGetExternalReset = false;
	boolean toGetInitialCondition = false;
	boolean toGetLowerValue = false;
	boolean toGetUpperValue = false;
	boolean toGetLowerLimit = false;
	boolean toGetUpperLimit = false;
	boolean toGetGotoTag = false;
	boolean toGetOperator = false;
	
	boolean inLine = false;
	boolean toGetSrcBlock = false;
	boolean toGetSrcPort = false;
	boolean toGetDstBlock = false;
	boolean toGetDstPort = false;
	boolean toGetPoints = false;
	boolean inSrc = false;
	boolean inDst = false;
	boolean lastSrc = false;
	int branchDeep = 0;
	int nextPoints = 0;
	String srcBlock = new String();
	String srcPort = new String();
	String dstBlock = new String();
	String dstPort = new String();
	ArrayList<String> points = new ArrayList<String>();
	
		
	/**
	 * Default Constructor
	*/
	public SLContentHandler() 
	{
		super();
		// Default locator
		locator_ = new LocatorImpl();
	}

	/**
	 * Definition du locator qui permet a tout moment pendant l'analyse, de
	 * localiser le traitement dans le flux. Le locator par defaut indique, par
	 * exemple, le numero de ligne et le numero de caractere sur la ligne.
	 * 
	 * @author smeric
	 * @param value
	 *            le locator a utiliser.
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator value) 
	{
		locator_ = value;
	}

	/**
	 * Evenement envoye au demarrage du parse du flux xml.
	 * 
	 * @throws SAXException
	 *             en cas de probleme quelquonque ne permettant pas de se lancer
	 *             dans l'analyse du document.
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	
	public void startDocument() throws SAXException 
	{
		sxData.printSupported();
		
		System.out.println(">> STEP 2. Starting translation from SimuLink to SpaceEx format . . .");
		System.out.println("  >> 2.1. Begin.");
		System.out.println("                From SL xml --> Internal Data Structure.");
		System.out.println();	
	}
	
	/**
	 * Evenement envoye a la fin de l'analyse du flux xml.
	 * 
	 * @throws SAXException
	 *             en cas de probleme quelquonque ne permettant pas de
	 *             considerer l'analyse du document comme etant complete.
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException 
	{
		System.out.println("  >> 2.1. Done.");
		System.out.println();
		System.out.println("  >> 2.2. Begin.");
		System.out.println("              Postprocessing Internal Data Structure.");

		sxData.postProcess();
		System.out.println("  >> 2.2. Done.");
		System.out.println();
		
		
		// Debug Info
		SXDebug sxDebug = new SXDebug(sxData);
		sxDebug.printModel(printSystemsInfo, printBlocksInfo, printVarsInfo);
		
		System.out.println(">> STEP 3.   Write the corresponding SpaceEx Model on file.");
		System.out.println("  >> 3.1 Begin.");
		System.out.println("             From the Internal Data Structure --> .xml SpaceEx Model.");
	
		SXBuilder sxBuild = new SXBuilder(sxData);
		sxBuild.extractSXModel();
		
		System.out.println("  >> 3.1 Done.");
		
		System.out.println("  >> 3.2 Begin.");
		System.out.println("             From the Internal Data Structure --> .xml SpaceEx Model Configuration.");
	
		sxBuild.extractSXModelConfiguration();
		
		System.out.println("  >> 3.2 Done.");

	}

	/**
	 * Debut de traitement dans un espace de nommage.
	 * 
	 * @param prefixe
	 *            utilise pour cet espace de nommage dans cette partie de
	 *            l'arborescence.
	 * @param URI
	 *            de l'espace de nommage.
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String prefix, String URI)
			throws SAXException 
	{
		System.out.println("Traitement de l'espace de nommage : " + URI
				+ ", prefixe choisi : " + prefix);
	}

	/**
	 * Fin de traitement de l'espace de nommage.
	 * 
	 * @param prefixe
	 *            le prefixe choisi a l'ouverture du traitement de l'espace
	 *            nommage.
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException 
	{
		System.out.println("Fin de traitement de l'espace de nommage : "
				+ prefix);
	}

	/**
	 * Evenement recu a chaque fois que l'analyseur rencontre une balise xml
	 * ouvrante.
	 * 
	 * @param nameSpaceURI
	 *            l'url de l'espace de nommage.
	 * @param localName
	 *            le nom local de la balise.
	 * @param rawName
	 *            nom de la balise en version 1.0
	 *            <code>nameSpaceURI + ":" + localName</code>
	 * @throws SAXException
	 *             si la balise ne correspond pas a ce qui est attendu, comme
	 *             par exemple non respect d'une dtd.
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String nameSpaceURI, String localName,
			String rawName, Attributes attributs) throws SAXException 
	{	
		if (!"".equals(nameSpaceURI)) 
		{ // espace de nommage particulier
		   System.out.println(" nameSpaceURI: " + nameSpaceURI);
	    }
		
		// Get SL Model Name and create the Main System 
		if (localName.equals("Model"))
		{
			sxData.setName(attributs.getValue("Name"));
			sxData.setFileName(attributs.getValue("Name"));
		}
		// Tag for the StartTime Simulation
		else if (localName.equals("P") && attributs.getValue("Name").equals("StartTime"))
			toGetStartTime = true;
		// Tag for the StopTime Simulation
		else if (localName.equals("P") && attributs.getValue("Name").equals("StopTime"))
			toGetStopTime = true;
		// Tag for the Simulation Max Step Size
		else if (localName.equals("P") && attributs.getValue("Name").equals("MaxStep"))
			toGetMaxStep = true;
		// Tag for the Simulation Relative Tolerance
		else if (localName.equals("P") && attributs.getValue("Name").equals("RelTol"))
			toGetRelTol = true;
		// Tag for the Simulation Absolute Tolerance
		else if (localName.equals("P") && attributs.getValue("Name").equals("AbsTol"))
			toGetAbsTol = true;
		
		// Tag for the Blocks Parameter Defaults
		else if (localName.equals("BlockParameterDefaults"))
			toGetDefault = true;
			
		else if (localName.equals("System"))
		{
			if (mainSys)
			{
				// Create Main System
				sys = new SLSystem(sxData.getName(), -1);
				mainSys = false;
			}
			else
			{
				sxData.getCurrSys().addBlock(block);
				// Create SubSystem
				sys = new SLSystem(block.getName(), sxData.getCurrSysIndex());
				sys.setBlockMirror(block.isBlockMirror());
			}
		
			sxData.addSys(sys);
		}
		
		// Single Block
		else if (localName.equals("Block"))
		{

			inBlock = true;
			if (toGetDefault)
				// Default Parameter
				block = new SLBlock(attributs.getValue("BlockType"), attributs.getValue("BlockType"), sxData.getSupportedBlocks(), hShift);
			else 
				// Real Block
				block = new SLBlock(attributs.getValue("Name").replace(" ", "_"), attributs.getValue("BlockType"), sxData.getSupportedBlocks(), hShift);
		}
			
		else if (localName.equals("P") && (inBlock))
		{
			// Tag for the Attribute Ports
			if (attributs.getValue("Name").equals("Ports"))
				toGetPorts = true;
			// Tag for the Attribute Position		
			else if (attributs.getValue("Name").equals("Position"))
				toGetPosition = true;
			// Tag for the Attribute BlockMirror		
			else if (attributs.getValue("Name").equals("BlockMirror"))
				toGetBlockMirror = true;
			// Tag for the Attribute Value
			else if (attributs.getValue("Name").equals("Value"))
				toGetValue = true;
			// Tag for the Attribute Gain
			else if (attributs.getValue("Name").equals("Gain"))
				toGetGain = true;
			// Tag for the Attribute Inputs
			else if (attributs.getValue("Name").equals("Inputs"))
				toGetInputs = true;
			// Tag for the Attribute Outputs
			else if (attributs.getValue("Name").equals("Outputs"))
				toGetOutputs = true;
			// Tag for the Attribute Criteria
			else if (attributs.getValue("Name").equals("Criteria"))
				toGetCriteria = true;
			// Tag for the Attribute Threshold
			else if (attributs.getValue("Name").equals("Threshold"))
				toGetThreshold = true;
			// Tag for the Attribute ExternalReset
			else if (attributs.getValue("Name").equals("ExternalReset"))
				toGetExternalReset = true;
			// Tag for the Integrator Initial Condition
			else if (attributs.getValue("Name").equals("InitialCondition"))
				toGetInitialCondition = true;
			// Tag for the Attribute LowerValue
			else if (attributs.getValue("Name").equals("LowerValue"))
				toGetLowerValue = true;
			// Tag for the Attribute UpperValue
			else if (attributs.getValue("Name").equals("UpperValue"))
				toGetUpperValue = true;
			// Tag for the Attribute LowerLimit (Saturate)
			else if (attributs.getValue("Name").equals("LowerLimit"))
				toGetLowerLimit = true;
			// Tag for the Attribute UpperLimit (Saturate)
			else if (attributs.getValue("Name").equals("UpperLimit"))
				toGetUpperLimit = true;
			// Tag for the Attribute GotoTag
			else if (attributs.getValue("Name").equals("GotoTag"))
				toGetGotoTag = true;
			// Tag for the Attribute Operator (Trig and Logic)
			else if (attributs.getValue("Name").equals("Operator"))
				toGetOperator = true;
		}
		// Tag for the Connection among blocks
		else if (localName.equals("Line"))
			inLine = true;
	
		else if (inLine && localName.equals("P"))
		{
			// Tag for the Source Block Line
			if (attributs.getValue("Name").equals("SrcBlock"))
				toGetSrcBlock = true;
			// Tag for the Source Port Line
			else if (attributs.getValue("Name").equals("SrcPort"))
				toGetSrcPort = true;
			// Tag for the Destination Block Line
			else if (attributs.getValue("Name").equals("DstBlock"))
				toGetDstBlock = true;
			// Tag for the Destination Port Line
			else if (attributs.getValue("Name").equals("DstPort"))
				toGetDstPort = true;
			// Tag for the Connection Points
		 	else if (attributs.getValue("Name").equals("Points"))
				toGetPoints = true;
		}
		// Tag for the Connection Branching
		else if (inLine && localName.equals("Branch"))
			branchDeep ++;
	}

	/**
	 * Evenement recu a chaque fermeture de balise.
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(String nameSpaceURI, String localName, String rawName)
			throws SAXException 
	{
		// Tag for the Block Parameter Defaults
		if (localName.equals("BlockParameterDefaults"))
			toGetDefault = false;
		
		// End of the Subsystem
		else if (localName.equals("System"))
		{
			// The current system now is the parent
			sxData.goToParent();
			toInsertBlock = false;
		}
		// End of the Block	
		else if (localName.equals("Block"))
		{
			if (toGetDefault)
				// Add the Block with the Default Parameters
				sxData.addDBlock(block);
			else if (toInsertBlock)
				// Add the effective Block
				sxData.getCurrSys().addBlock(block);
			
			toInsertBlock = true;
			inBlock = false;
		}
		// End of the Connections among Blocks
		else if (localName.equals("Line"))
			inLine = false;
		// End of a single Branch
		else if (localName.equals("Branch"))
		{
			branchDeep --;
			// Change the next insertion point
			nextPoints = branchDeep;
		}
	}

	/**
	 * Evenement recu a chaque fois que l'analyseur rencontre des caracteres
	 * (entre deux balises).
	 * 
	 * @param ch
	 *            les caracteres proprement dits.
	 * @param start
	 *            le rang du premier caractere a traiter effectivement.
	 * @param end
	 *            le rang du dernier caractere a traiter effectivement
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		String chars = new String(ch, start, length);
		
		// Get StartTime Simulation
		if (toGetStartTime)
		{
			sxData.setStartTime(chars);
			toGetStartTime = false;			
		}
		// Get StopTime Simulation
		else if (toGetStopTime)
		{
			sxData.setStopTime(chars);
			toGetStopTime = false;			
		}
		// Get MaxStep Size
		if (toGetMaxStep)
		{
			sxData.setMaxStep(chars);
			toGetMaxStep = false;			
		}
		// Get Relative Tolerance
		else if (toGetRelTol)
		{
			sxData.setRelTol(chars);
			toGetRelTol = false;			
		}
		// Get Relative Tolerance
		else if (toGetAbsTol)
		{
			sxData.setAbsTol(chars);
			toGetAbsTol = false;			
		}
				
		// Get Ports
		else if (toGetPorts)
		{
			block.setPorts(chars);
			toGetPorts = false;			
		}

		// Get Position
		else if (toGetPosition)
		{
		    block.setPosition(chars);
			toGetPosition = false;			
		}
		
		// Get BlockMirror
		else if (toGetBlockMirror)
		{
			if (chars.equals("on"))
			{
				sxData.setMirrorBlockOn();
				block.setBlockMirror(true);
			}
			toGetBlockMirror = false;			
		}
		
		// Get Value
		else if (toGetValue)
		{
			block.setValue(chars);
			toGetValue = false;			
		}
		// Get Gain
		else if (toGetGain)
		{
			block.setGain(chars);
			toGetGain = false;			
		}
		// Get Inputs
		else if (toGetInputs)
		{
			block.setInputs(chars);
			toGetInputs = false;
		}
		// Get Outputs
		else if (toGetOutputs)
		{
			block.setOutputs(chars);
			toGetOutputs = false;			
		}
		// Get Criteria
		else if (toGetCriteria)
		{
			block.setCriteria(chars);
			toGetCriteria = false;			
		}
		// Get Threshold
		else if (toGetThreshold)
		{
			block.setThreshold(chars);
			toGetThreshold = false;			
		}
		// Get ExternalReset
		else if (toGetExternalReset)
		{
			block.setExternalReset(chars);
			toGetExternalReset = false;			
		}
		// Get the Integrator Initial Cond
		else if(toGetInitialCondition)
		{
			block.setInitialCondition(chars);
			toGetInitialCondition = false;
		}
		// Get LowerValue
		else if (toGetLowerValue)
		{
			block.setLowerValue(chars);
			toGetLowerValue = false;			
		}
		// Get UpperValue
		else if (toGetUpperValue)
		{
			block.setUpperValue(chars);
			toGetUpperValue = false;			
		}
		// Get LowerValue
		else if (toGetLowerLimit)
		{
			block.setLowerLimit(chars);
			toGetLowerLimit = false;			
		}
		// Get UpperLimit
		else if (toGetUpperLimit)
		{
			block.setUpperLimit(chars);
			toGetUpperLimit = false;			
		}
		// Get GotoTag
		else if (toGetGotoTag)
		{
			block.setGotoTag(chars);
			toGetGotoTag = false;			
		}
		// Get Trigonometry and Logic Operator
		else if (toGetOperator)
		{
			block.setOperator(chars);
			toGetOperator = false;			
		}
		// Get the Source Block Line
		else if (toGetSrcBlock)
		{
			srcBlock = chars.replace(" ", "_");
			toGetSrcBlock = false;
		}
		// Get the Source Port Line
		else if (toGetSrcPort)
		{
			srcPort = chars;
			toGetSrcPort = false;
			lastSrc = true;
		}
		// Get the Destination Block Line
		else if (toGetDstBlock)
		{
			dstBlock = chars.replace(" ", "_");
			toGetDstBlock = false;
			lastSrc = false;
		}
		// Get the Destination Port Line
		else if (toGetDstPort)
		{
			dstPort = chars;
			// Add the Mapping (srcBlock+srcPort) --> (dstBlock+dstPort)
			sxData.getCurrSys().setMap(srcBlock, srcPort, dstBlock, dstPort);
			// add Points (line connection shape) to the Dst Variable
			sxData.getCurrSys().addDstPoints(dstBlock, dstPort, points, nextPoints);
			toGetDstPort = false;
		}
		else if (toGetPoints)
		{
			if (lastSrc)
			{
				// add Points (line connection shape) to the Src Variable
				sxData.getCurrSys().addSrcPoints(srcBlock, srcPort, chars); 
				lastSrc = false;
			}
			else 
			{
				// Accumulate Points into an Array
				points.add(nextPoints, chars);
				nextPoints++;
			}
			toGetPoints = false;
		}
	}
	

	/**
	 * Recu chaque fois que des caracteres d'espacement peuvent etre ignores au
	 * sens de XML. C'est a dire que cet evenement est envoye pour plusieurs
	 * espaces se succedant, les tabulations, et les retours chariot se
	 * succedants ainsi que toute combinaison de ces trois types d'occurrence.
	 * 
	 * @param ch
	 *            les caracteres proprement dits.
	 * @param start
	 *            le rang du premier caractere a traiter effectivement.
	 * @param end
	 *            le rang du dernier caractere a traiter effectivement
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int end)
			throws SAXException {
		System.out.println("espaces inutiles rencontres : ..."
				+ new String(ch, start, end) + "...");
	}

	/**
	 * Rencontre une instruction de fonctionnement.
	 * 
	 * @param target
	 *            la cible de l'instruction de fonctionnement.
	 * @param data
	 *            les valeurs associees a cette cible. En general, elle se
	 *            presente sous la forme d'une serie de paires nom/valeur.
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
	 *      java.lang.String)
	 */
	public void processingInstruction(String target, String data)
			throws SAXException {
		System.out.println("Instruction de fonctionnement : " + target);
		System.out.println("  dont les arguments sont : " + data);
	}

	/**
	 * Recu a chaque fois qu'une balise est evitee dans le traitement a cause
	 * d'un probleme non bloque par le parser. Pour ma part je ne pense pas que
	 * vous en ayez besoin dans vos traitements.
	 * 
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
		// Je ne fais rien, ce qui se passe n'est pas franchement normal.
		// Pour eviter cet evenement, le mieux est quand meme de specifier une
		// dtd pour vos
		// documents xml et de les faire valider par votre parser.
	}

	private Locator locator_;
	

}
