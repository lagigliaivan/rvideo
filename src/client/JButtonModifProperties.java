package client;

import javax.swing.JButton;
import javax.swing.JList;

public class JButtonModifProperties extends JButton implements ActionButton {

	private static final long serialVersionUID = 1L;

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		JList lista = frmPrincipal.getJPanelListaCamaras().getJListCamarasRaw();
		ListItem item = (ListItem)lista.getModel().getElementAt(lista.getSelectedIndex());
		String camProperties = item.getValue();
		String cam = frmPrincipal.getXmlParser().getCamProperties(frmPrincipal.getFileXmlCamaras(), camProperties);

		DlgGetDescrip descrip = new DlgGetDescrip(cam,camProperties);
		descrip.setXmlParser(frmPrincipal.getXmlParser());
		descrip.setFileXml(frmPrincipal.getFileXmlCamaras());
		descrip.setParent(frmPrincipal);
		descrip.getJDialogDesc().setVisible(true);
		return 0;
	}

}
