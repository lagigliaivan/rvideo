package client;

import javax.swing.JMenuItem;

public class JMenuItemAbout extends JMenuItem implements ActionButton {

	private static final long serialVersionUID = 917271628523835474L;

	@Override
	public int performAction(Object data) throws Exception {
		
		FrmAbout about = new FrmAbout();
		about.getJFrameAbout().setVisible(true);
		return 0;
	}
	
}
