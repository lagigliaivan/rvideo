package client;


import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
public class ThrDialogRegister extends Thread{

	private JDialog jDialogConect = null;  //  @jve:decl-index=0:visual-constraint="120,14"
	private JPanel jContentPaneDialog = null;
	private JLabel jLabelConexion = null;
	private SipManager sipManager = null;
	private int sipConexionIndex = 0;
	private FrmPrincipal parent;
	
	private String nombreUsuario = null;
	private String dominio = null;
	private char[] pass = null; 
	
	private int x = 0;
	private int y = 0;
	
	GraphicsEnvironment gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
	
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

	public ThrDialogRegister(SipManager sipManager,FrmPrincipal parent){
		this.sipManager = sipManager;
		this.parent = parent;
	}
	
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
			jDialogConect.setLocation(getX() ,getY());
			jDialogConect.setContentPane(getJContentPaneDialog());
			jDialogConect.setResizable(false);
			
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
			jLabelConexion = new JLabel();
			jLabelConexion.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelConexion.setText("Conecting....");
			jLabelConexion.setIcon(new ImageIcon(getClass().getResource("/img/flechaVerde.gif")));
			jContentPaneDialog = new JPanel();
			jContentPaneDialog.setLayout(new BorderLayout());
			jContentPaneDialog.add(jLabelConexion, java.awt.BorderLayout.CENTER);
		}
		return jContentPaneDialog;
	}
	
	public void run(){
		
		parent.getJFrameVentanaPrincipal().setEnabled(false);
		parent.getJFrameVentanaPrincipal().setFocusable(false);
		getJDialogConect().setVisible(true);
		
		try {
		
			sipManager.start();
		
		} catch (ExcGeneric comExc) {
			jLabelConexion.setText("Error: " + comExc.getMessage());
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
			parent.getSipManager().reset();
			parent.setSipManager(null);
			getJDialogConect().setVisible(false);
			parent.getJFrameVentanaPrincipal().setEnabled(true);
			return;
		}
		
		try {
			jLabelConexion.setText("Stun proccess...");
			sipManager.stunDetection();
		} catch (ExcGeneric commExc) {
				
			jLabelConexion.setText("Error : " + commExc );
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
		}
		if(sipManager.getIpPublicAddress().contains("127")){
			jLabelConexion.setText("Error: loop dir as public");
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
			getJDialogConect().setVisible(false);
			parent.getJFrameVentanaPrincipal().setEnabled(true);
			parent.getJFrameVentanaPrincipal().setState(2);
			parent.getSipManager().reset();
			return;
		}
		jLabelConexion.setText("Setting ip public as " + sipManager.getIpPublicAddress());
		
		try {Thread.sleep(3000);} catch (InterruptedException e) {}
		
		try {
			sipConexionIndex = sipManager.createRegisterSipConexion(getNombreUsuario(),getDominio(),getPass());
		} catch (ExcGeneric excGeneric) {
			jLabelConexion.setText(excGeneric.getMessage());
			parent.getSipManager().reset();
			parent.setSipManager(null);
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
			return;
		}
			
		jLabelConexion.setText("Registering with sip server..." );
		
		try{
			sipManager.sendRegisterRequest(sipConexionIndex);
						
		}catch(ExcGeneric commExc){
			jLabelConexion.setText("Fatal error: " + commExc.getMessage() );
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
			getJDialogConect().setVisible(false);
			parent.getJFrameVentanaPrincipal().setEnabled(true);
			parent.getJFrameVentanaPrincipal().setState(2);
			return;
		}
						
		getJDialogConect().setVisible(false);
		parent.getJMenuBarVentanaPrincipal().setVisible(true);
		parent.getJFrameVentanaPrincipal().setVisible(true);
		parent.getJFrameVentanaPrincipal().setEnabled(true);
		parent.showList();
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

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	
}
