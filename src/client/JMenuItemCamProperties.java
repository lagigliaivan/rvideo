package client;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class JMenuItemCamProperties extends JMenuItem implements ActionButton {

	private static final long serialVersionUID = 2665010713102569806L;

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		JList lista = frmPrincipal.getJPanelListaCamaras().getJListCamarasRaw();
		ListItem item = (ListItem)lista.getModel().getElementAt(lista.getSelectedIndex());
		String propertiesCam = item.getValue();
		String cam = frmPrincipal.getXmlParser().getCamProperties(frmPrincipal.getFileXmlCamaras(), propertiesCam);
		JOptionPane.showMessageDialog(null,"propiedades: " + cam);
		
		return 0;
	}

}
