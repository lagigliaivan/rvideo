package client;

import javax.swing.JMenuItem;

public class JMenuItemRefreshList extends JMenuItem implements ActionButton {

	
	private static final long serialVersionUID = -3565563069609801374L;

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		frmPrincipal.showList();
		return 0;
	}

}
