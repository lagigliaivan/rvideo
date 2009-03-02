package client;

import javax.swing.JButton;

public class JButtonConnect extends JButton implements ActionButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int performAction(Object data) {
		
		((FrmPrincipal)data).validateAndConnect();
		return 0;
	}

}
