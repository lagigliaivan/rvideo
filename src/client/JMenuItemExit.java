package client;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class JMenuItemExit extends JMenuItem implements ActionButton {

	private static final long serialVersionUID = 8684488650586089044L;

	@Override
	public int performAction(Object data) throws Exception {
		
		int result = JOptionPane.showConfirmDialog(null,"Seguro que desea salir?",
				"Confirmar",JOptionPane.YES_NO_OPTION);
		if( result == 0){
			System.exit(0);
		}
		
		return 0;
	}

}
