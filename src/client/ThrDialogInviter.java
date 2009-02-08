/**
 * Esta clase es un hilo que envia un invite a la camara seleccionada.
 * @author ivan
 * @version 1.0 05 Setiembre 2006
 */
package client;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class ThrDialogInviter extends Thread {

	private JDialog jDialogConect = null;  //  @jve:decl-index=0:visual-constraint="120,14"
	private JPanel jContentPaneDialog = null;
	private JLabel jLabel = null;
	private SipManager sipManager = null;
	private FrmPrincipal parent = null;
	private String nombreUsuario = null;
	private char[] pass = null; 
	private int x = 0;
	private int y = 0;

	/**
	 * This method initializes jDialogConect	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	private JDialog getJDialogConect() {
		
		if (jDialogConect == null) {
			
			jDialogConect = new JDialog();
			jDialogConect.setTitle("Conexion");
			jDialogConect.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
			jDialogConect.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
			jDialogConect.setSize(new java.awt.Dimension(400,120));
			jDialogConect.setLocation(new java.awt.Point(200,200));
			jDialogConect.setResizable(false);
			jDialogConect.setContentPane(getJContentPaneDialog());
			jDialogConect.setModal(false);
			jDialogConect.setLocation(getX(), getY());
			
		}
		return jDialogConect;
	}

	/**
	 * This method initializes jContentPaneDialog	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPaneDialog() {
		if (jContentPaneDialog == null) {
			jLabel = new JLabel();
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jLabel.setText("Conectando....");
			jLabel.setIcon(new ImageIcon(getClass().getResource("/img/camara.gif")));
			jContentPaneDialog = new JPanel();
			jContentPaneDialog.setLayout(new BorderLayout());
			jContentPaneDialog.add(jLabel, java.awt.BorderLayout.CENTER);

		}
		return jContentPaneDialog;
	}

	public void run(){
		int descriptorSipConexion = 0;
		getJDialogConect().setVisible(true);
		//getParent().getJFrameVentanaPrincipal().setEnabled(false);
		try {
			descriptorSipConexion = sipManager.createInviteSipConexion(getNombreUsuario());
		} catch (ExcGeneric regExc) {
			jLabel.setText("Error " + regExc.getMessage());
			try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
			getJDialogConect().setVisible(false);
			//getParent().getJFrameVentanaPrincipal().setEnabled(true);
			return;
		}
		jLabel.setText("Inviting to " + getNombreUsuario() );
		try{
			sipManager.sendInviteRequest(descriptorSipConexion);
		}catch(ExcGeneric comExc){
			jLabel.setText("Error: " + comExc.getMessage());
			try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
			getJDialogConect().setVisible(false);
			//getParent().getJFrameVentanaPrincipal().setEnabled(true);
			return;
		}
		getJDialogConect().setVisible(false);
		//getParent().getJFrameVentanaPrincipal().setEnabled(true);
	}
	
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public char[] getPass() {
		return pass;
	}

	public void setPass(char[] pass) {
		this.pass = pass;
	}

	public ThrDialogInviter(SipManager sipManager,FrmPrincipal parent){
		this.sipManager = sipManager;
		this.parent = parent ; 
	}

	public FrmPrincipal getParent() {
		return parent;
	}

	public void setParent(FrmPrincipal parent) {
		this.parent = parent;
	}


}
