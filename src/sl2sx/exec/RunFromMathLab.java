package sl2sx.exec;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import sl2sx.SLContentHandler;
import fr.imag.verimag.spaceex_moe.utils.Utility;

public class RunFromMathLab {

	public static File getLoadFile() {
		File rf = null;
		int returnValue = JFileChooser.CANCEL_OPTION;
		JFileChooser fileReadChooser = null;

		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		fileReadChooser = new JFileChooser();
		FileFilter fileFilter = new FileFilter() {
			/**
			 * @see javax.swing.filechooser.FileFilter#accept(File)
			 */
			public boolean accept(File f) {
				if (f == null)
					return false;
				if (f.getName() == null)
					return false;
				if (f.getName().endsWith(".xml"))
					return true;
				if (f.isDirectory())
					return true;

				return false;
			}

			@Override
			public String getDescription() {
				return "description";
			}

		};
		fileReadChooser.setFileFilter(fileFilter);
		UIManager.put("FileChooser.readOnly", Boolean.FALSE);

		fileReadChooser.setSelectedFile(new File(""));
		returnValue = fileReadChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			rf = fileReadChooser.getSelectedFile();
		}

		return rf;
	}

	public static void parseTR(String uri) throws SAXException, IOException {

		XMLReader saxReader = XMLReaderFactory.createXMLReader();

		saxReader.setContentHandler(new SLContentHandler());
		saxReader.parse(uri);

	}

	/**
	 * @param args
	 */
	
	
	public static void main(String[] args) {

			//System.out.println("         *******************************************************************************************");
			//System.out.println("         *                                                                                         *");
			//System.out.println("         *           Welcome to SimuLink To SpaceEx Converter (SL2SX) ver 1.0                      *");
			//System.out.println("         *               				Stefano Minopoli							               *");
			//System.out.println("         *                                                                                         *");
			//System.out.println("         *******************************************************************************************");
			//System.out.println();
			//System.out.println();
			System.out.println("Welcome to SL2SX v.1.0");
		    
		    System.out.println(">> STEP 1. Choose the Simulink Diagram File in .xml Format...");
			System.out.println();		

			Utility.setLogEnable(false);

		try {
			//String fileName = "/Users/forets/Dropbox/recherche/synlin/tool/models/vanderpol/vanderpol-raw-simulink.xml";
			//String fileName;
			System.out.println(args[0]);
			// check whether the argument was passed
			parseTR(args[0]);
			//parseTR(getLoadFile().getAbsolutePath());
			
			// on the command line, we should do: 
			// java -jar SL2SX_terminal.jar /Users/forets/Dropbox/recherche/synlin/tool/models/vanderpol/vanderpol-raw-simulink.xml

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.exit(0);
	}
}
