package client;

import javax.swing.JButton;

public class JButtonConnect extends JButton implements ActionButton{

	private static final long serialVersionUID = 1L;

	@Override
	public int performAction(Object data) throws Exception{
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		
		frmPrincipal.validateAndConnect();
		return 0;
	}

}
