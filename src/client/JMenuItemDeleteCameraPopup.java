package client;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class JMenuItemDeleteCameraPopup extends JMenuItem implements ActionButton{
	
	private static final long serialVersionUID = 1L;

	public int performAction(Object data) throws Exception{
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		
		JList lista = frmPrincipal.getJPanelListaCamaras().getJListCamarasRaw();
		ListItem item = (ListItem)lista.getModel().getElementAt(lista.getSelectedIndex());
		String deletedCam = item.getValue();

		Object[] options = { "SI", "NO" };
		int eleccion = JOptionPane.showOptionDialog(null, 
				"Seguro que desea eliminar a " + deletedCam, "Warning",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);
		
		//"NO" was selected
		if(eleccion == 1){
			return 1;
		}

		Vector camaras = frmPrincipal.getXmlParser().getCamaras(frmPrincipal.getFileXmlCamaras());

		if(deletedCam == null || deletedCam.compareTo("") == 0){
			throw new Exception("no camera deleted");
		}
		String dataFile = "<camaras>\n";

		for(int i = 0 ; i < camaras.size();i++){

			if(((String)camaras.get(i)).compareTo((String)deletedCam)!=0){
				dataFile +="<camara>\n";
				dataFile += "<name>"+(String)camaras.get(i)+"</name>\n";
				dataFile += "<desc></desc>\n";
				dataFile +="</camara>\n";
			}

		}
		dataFile += "</camaras>\n"; 
		frmPrincipal.writeFileXml(frmPrincipal.getFileXmlCamaras(), dataFile);
		frmPrincipal.showList();	
	
		return 0;
	}
}
