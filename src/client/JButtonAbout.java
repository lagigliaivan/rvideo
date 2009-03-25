package client;

import javax.swing.JButton;

public class JButtonAbout extends JButton implements ActionButton {

	private static final long serialVersionUID = 1L;

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmAbout about = new FrmAbout();
		about.getJFrameAbout().setVisible(true);
		return 0;
	}
	
}
