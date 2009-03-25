package client;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class JButtonExit extends JButton implements ActionButton {

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
