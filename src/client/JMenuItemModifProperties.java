package client;

import javax.swing.JList;
import javax.swing.JMenuItem;

public class JMenuItemModifProperties extends JMenuItem implements ActionButton {

	private static final long serialVersionUID = -5801050260311187159L;

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		JList lista = frmPrincipal.getJPanelListaCamaras().getJListCamarasRaw();
		ListItem item = (ListItem)lista.getModel().getElementAt(lista.getSelectedIndex());
		String propertiesCam = item.getValue();
		String cam = frmPrincipal.getXmlParser().getCamProperties(frmPrincipal.getFileXmlCamaras(), propertiesCam);

		DlgGetDescrip descrip = new DlgGetDescrip(cam,propertiesCam);
		descrip.setXmlParser(frmPrincipal.getXmlParser());
		descrip.setFileXml(frmPrincipal.getFileXmlCamaras());
		descrip.setParent(frmPrincipal);
		descrip.getJDialogDesc().setVisible(true);
		return 0;
	}

}
