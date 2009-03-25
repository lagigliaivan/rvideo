package client;

import javax.swing.JButton;

public class JButtonRefreshList extends JButton implements ActionButton {

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		frmPrincipal.showList();
		return 0;
	}

}
