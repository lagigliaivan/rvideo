package consola;

import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class FrmAbout {

	private JFrame jFrameAbout = null;  //  @jve:decl-index=0:visual-constraint="227,12"
	private JTextArea jTextAreaAbout = null;
	/**
	 * This method initializes jFrameAbout	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public JFrame getJFrameAbout() {
		if (jFrameAbout == null) {
			Point point = Tools.getCenterPosition();
			jFrameAbout = new JFrame();
			jFrameAbout.setSize(new Dimension(374, 158));
			jFrameAbout.setContentPane(getJTextAreaAbout());
			jFrameAbout.setTitle("Acerca de Consola SIP");
			jFrameAbout.setLocation((int)point.getX()/2 - 100 , (int)point.getY()/2 - 100);
		}
		return jFrameAbout;
	}
	/**
	 * This method initializes jTextAreaAbout	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextAreaAbout() {
		if (jTextAreaAbout == null) {
			jTextAreaAbout = new JTextArea();
			jTextAreaAbout.setText("Visualizador de camaras -- Consola SIP  \n Version: 0.1 \n Compilacion: 16 Oct. 2006");
			jTextAreaAbout.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			jTextAreaAbout.setEditable(false);
			jTextAreaAbout.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			jTextAreaAbout.setWrapStyleWord(false);
		}
		return jTextAreaAbout;
	}

}
