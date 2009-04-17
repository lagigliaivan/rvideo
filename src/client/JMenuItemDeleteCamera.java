package client;

import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class JMenuItemDeleteCamera extends JMenuItem implements ActionButton{

	private static final long serialVersionUID = 2051389488787189769L;

	public int performAction(Object data) throws Exception {
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		
		Vector camaras = frmPrincipal.getXmlParser().getCamaras(frmPrincipal.getFileXmlCamaras());
		String possibleValues[] = new String[camaras.size()];
		camaras.copyInto(possibleValues);

		Object deletedCam = JOptionPane.showInputDialog(null,
				"Seleccione la camara a eliminar", "Input",
				JOptionPane.INFORMATION_MESSAGE, null,
				possibleValues,
				possibleValues[0]);


		if(deletedCam==null){
			throw new Exception("No camera selected");
		}
		//TODO avoid this type of IO use, the file format and content must not be hardcoded.
		String dataFile = "<camaras>\n";

		for(int i = 0 ; i < camaras.size();i++){

			if(((String)camaras.get(i)).compareTo((String)deletedCam)!=0){
				dataFile +="<camara>\n";
				dataFile += "<name>"+(String)camaras.get(i)+"</name>\n";
				dataFile += "<desc></desc>\n";
				dataFile +="</camara>\n";
			}
		}
		dataFile += "</camaras>"; 
		frmPrincipal.writeFileXml(frmPrincipal.getFileXmlCamaras(), dataFile);
		frmPrincipal.showList();
		
		return 0;
	}
	
}
