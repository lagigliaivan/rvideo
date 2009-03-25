package client;

import javax.swing.JButton;

public class JButtonConfigure extends JButton implements ActionButton {

	private static final long serialVersionUID = 1L;

	@Override
	public int performAction(Object data) throws Exception{
		
		FrmPrincipal frmPrincipal = (FrmPrincipal)data;
		FrmConfig ventanaConfig = new FrmConfig();
		ventanaConfig.getJFrameConfig().setVisible(true);
		return 0;
	}
}
