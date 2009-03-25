package client;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class JButtonProperties extends JButton implements ActionButton {

	private static final long serialVersionUID = 1L;

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
