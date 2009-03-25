package client;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class JButtonAddCamera extends JButton implements ActionButton {

	private static final long serialVersionUID = 1L;

	@Override
	public int performAction(Object data) throws Exception{
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		String newCam = "";

		while(newCam != null && newCam.compareTo("")  ==  0 ){

			newCam = JOptionPane.showInputDialog(null,"Ingrese Camara","Completar",JOptionPane.QUESTION_MESSAGE);

			if(newCam == null){
				throw new Exception("One camera name is needed to be added");
			}

			if(newCam.compareTo("") == 0 || !newCam.contains("@")||newCam.endsWith("@")|| newCam.startsWith("@")  )
			{
				JOptionPane.showMessageDialog(frmPrincipal.getJContentPaneVentanaPrincipal(),
						"El nombre de la camara debe tener la forma nombre@dominio",
						"info",
						JOptionPane.INFORMATION_MESSAGE);
				newCam = "";
			}	
		}

		/**
		 * Abre el archivo que contiene la lista de camaras
		 * y le agrega la camara nueva
		 */
		Vector camaras = frmPrincipal.getXmlParser().getCamaras(frmPrincipal.getFileXmlCamaras());

		String dataFile = "<camaras>\n";

		for(int i = 0 ; i < camaras.size();i++){
			dataFile += "<camara>\n";
			dataFile += "<name>"+(String)camaras.get(i)+"</name>\n"; 
			dataFile += "<desc></desc>\n";
			dataFile += "</camara>\n";
		}
		dataFile += "<camara>\n";
		dataFile += "<name>"+ newCam +"</name>\n";
		dataFile += "<desc></desc>\n";
		dataFile += "</camara>\n";
		dataFile +="</camaras>";

		frmPrincipal.writeFileXml(frmPrincipal.getFileXmlCamaras(), dataFile);
		/**
		 * refresca la lista
		 */
		frmPrincipal.showList();
		
		return 0;
	}
}
