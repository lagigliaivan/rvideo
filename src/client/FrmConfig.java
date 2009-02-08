package client;
/**
 * Esta clase intenta mostrar una ventana muy simple
 * para configurar la conexion a distintos servidores
 * 
 * @version 1.0 31 Agosto 2006
 * @author Ivan
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Cursor;



public class FrmConfig implements ActionListener  {
	
	private JFrame jFrameConfig = null;  
	
	private JDesktopPane jDesktopPane = null;
	private JPanel jPanel = null;
	private JLabel jLabelStunConfig = null;
	private JTextField jTextFieldStunServerAddress = null;
	private JLabel jLabelPuerto = null;
	private JTextField jTextFieldStunServerPort = null;
	private JPanel jPanelLocalConfig = null;
	private JLabel jLabelLocalIp = null;
	private JLabel jLabelPuertoLocal = null;
	private JTextField jTextFieldLocalHostPort = null;
	private JPanel jPanelSipConfig = null;
	private JLabel jLabelProxySipConfig = null;
	private JTextField jTextFieldSipProxyAddress = null;
	private JLabel jLabelPuertoSipProxy = null;
	private JTextField jTextFieldSipProxyPort = null;
	private JLabel jLabelTituloStun = null;
	private JLabel jLabelTituloLocal = null;
	private JLabel jLabelTituloProxySip = null;
	private JButton jButtonAceptar = null;
	private JButton jButtonCancelar = null;
	private static String STUN_ADDRESS = "stun.xten.net";
	private static final int STUN_PORT = 5060;
	private static final int LOCALHOST_PORT = 5061;
	private static final String LOCALHOST_ADDRESS = "localhost";
	private static final String SIPPROXY_ADDRESS = "proxy01.sipphone.com";
	private static final int SIPPROXY_PORT = 5060;
	private static final String FILE_CONFIG = System.getProperty("user.dir") + File.separator + "config.xml";
	
	private JComboBox jComboBoxLocalHostAddress = null;
	private Enumeration <NetworkInterface> interfaces = null;
	private XmlConfigParser xmlConfigParser = new XmlConfigParser(FILE_CONFIG);
	/**
	 * This method initializes jFrameConfig	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public JFrame getJFrameConfig() {
		if (jFrameConfig == null) {
			int x;
			int y;
			jFrameConfig = new JFrame();
			jFrameConfig.setSize(new java.awt.Dimension(405,374));
			jFrameConfig.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/editorpane_obj.gif")));
			jFrameConfig.setResizable(false);
			jFrameConfig.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			jFrameConfig.setContentPane(getJDesktopPane());
			jFrameConfig.setTitle("Configuracion");
			x = Tools.getCenterPosition().x/2 - 100;
			y = Tools.getCenterPosition().y/5 + 100;
			jFrameConfig.setLocation(x, y);
						
		}
		try {
			xmlConfigParser.parse();
		} catch (ExcGeneric e) {
			return jFrameConfig;
		}
		
		getJTextFieldLocalHostPort().setText(Integer.toString(xmlConfigParser.getLocalHostPort()));
					
		getJTextFieldStunServerAddress().setText(xmlConfigParser.getStunServerAddress());
		getJTextFieldStunServerPort().setText(Integer.toString(xmlConfigParser.getStunServerPort()));
		
		getJTextFieldSipProxyAddress().setText(xmlConfigParser.getSipProxyAddress());
		getJTextFieldSipProxyPort().setText(Integer.toString(xmlConfigParser.getSipProxyPort()));
		
		return jFrameConfig;	
	}
	
	/**
	 * This method initializes jDesktopPane	
	 * 	
	 * @return javax.swing.JDesktopPane	
	 */
	private JDesktopPane getJDesktopPane() {
		
		if (jDesktopPane == null) {
			jLabelTituloProxySip = new JLabel();
			jLabelTituloProxySip.setBounds(new java.awt.Rectangle(90,220,251,16));
			jLabelTituloProxySip.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabelTituloProxySip.setText("Configuracion Proxy SIP");
			jLabelTituloLocal = new JLabel();
			jLabelTituloLocal.setBounds(new java.awt.Rectangle(85,129,256,16));
			jLabelTituloLocal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabelTituloLocal.setText("Configuracion Host");
			jLabelTituloStun = new JLabel();
			jLabelTituloStun.setBounds(new java.awt.Rectangle(80,30,258,16));
			jLabelTituloStun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jLabelTituloStun.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabelTituloStun.setText("Configuracion Stun Server");
			jDesktopPane = new JDesktopPane();
			jDesktopPane.setBackground(new java.awt.Color(238,238,238));
			jDesktopPane.add(getJPanel(), null);
			jDesktopPane.add(getJPanelLocalConfig(), null);
			jDesktopPane.add(getJPanelSipConfig(), null);
			jDesktopPane.add(jLabelTituloStun, null);
			jDesktopPane.add(jLabelTituloLocal, null);
			jDesktopPane.add(jLabelTituloProxySip, null);
			jDesktopPane.add(getJButtonAceptar(), null);
			jDesktopPane.add(getJButtonCancelar(), null);
		}
		return jDesktopPane;
	
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 1;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.gridy = 1;
			jLabelPuerto = new JLabel();
			jLabelPuerto.setText("Puerto");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(0,0,0,6);
			gridBagConstraints.gridy = 0;
			jLabelStunConfig = new JLabel();
			jLabelStunConfig.setText("Stun Server");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setBounds(new java.awt.Rectangle(11,55,357,63));
			jPanel.add(jLabelStunConfig, gridBagConstraints);
			jPanel.add(getJTextFieldStunServerAddress(), gridBagConstraints6);
			jPanel.add(jLabelPuerto, gridBagConstraints7);
			jPanel.add(getJTextFieldStunServerPort(), gridBagConstraints8);
		}
		return jPanel;
	}
	
	/**
	 * This method initializes jTextFieldStunServer	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldStunServerAddress() {
		if (jTextFieldStunServerAddress == null) {
			jTextFieldStunServerAddress = new JTextField();
			jTextFieldStunServerAddress.setText("stun.xten.net");
		}
		return jTextFieldStunServerAddress;
	}
	
	/**
	 * This method initializes jTextFieldPuertoStun	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldStunServerPort() {
		if (jTextFieldStunServerPort == null) {
			jTextFieldStunServerPort = new JTextField();
			jTextFieldStunServerPort.setText("3478");
		}
		return jTextFieldStunServerPort;
	}
	
	/**
	 * This method initializes jPanelLocalConfig	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelLocalConfig() {
		if (jPanelLocalConfig == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new java.awt.Insets(0,0,0,5);
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 1;
			jLabelPuertoLocal = new JLabel();
			jLabelPuertoLocal.setText("Puerto Local");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.gridy = 0;
			jLabelLocalIp = new JLabel();
			jLabelLocalIp.setText("IP Local");
			jPanelLocalConfig = new JPanel();
			jPanelLocalConfig.setLayout(new GridBagLayout());
			jPanelLocalConfig.setBounds(new java.awt.Rectangle(15,155,356,51));
			jPanelLocalConfig.add(jLabelLocalIp, gridBagConstraints9);
			jPanelLocalConfig.add(jLabelPuertoLocal, gridBagConstraints11);
			jPanelLocalConfig.add(getJTextFieldLocalHostPort(), gridBagConstraints12);
			jPanelLocalConfig.add(getJComboBoxLocalHostAddress(), gridBagConstraints5);
		}
		return jPanelLocalConfig;
	}
	
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldLocalHostPort() {
		if (jTextFieldLocalHostPort == null) {
			jTextFieldLocalHostPort = new JTextField();
		}
		return jTextFieldLocalHostPort;
	}
	
	/**
	 * This method initializes jPanelSipConfig	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelSipConfig() {
		if (jPanelSipConfig == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridy = 1;
			jLabelPuertoSipProxy = new JLabel();
			jLabelPuertoSipProxy.setText("Puerto");
			jLabelPuertoSipProxy.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new java.awt.Insets(0,0,0,14);
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints1.gridy = 0;
			jLabelProxySipConfig = new JLabel();
			jLabelProxySipConfig.setText("Proxy");
			jLabelProxySipConfig.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jLabelProxySipConfig.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jPanelSipConfig = new JPanel();
			jPanelSipConfig.setLayout(new GridBagLayout());
			jPanelSipConfig.setBounds(new java.awt.Rectangle(15,245,360,46));
			jPanelSipConfig.add(jLabelProxySipConfig, gridBagConstraints1);
			jPanelSipConfig.add(getJTextFieldSipProxyAddress(), gridBagConstraints2);
			jPanelSipConfig.add(jLabelPuertoSipProxy, gridBagConstraints3);
			jPanelSipConfig.add(getJTextFieldSipProxyPort(), gridBagConstraints4);
		}
		return jPanelSipConfig;
	}
	
	/**
	 * This method initializes jTextFieldOutboundProxy1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldSipProxyAddress() {
		if (jTextFieldSipProxyAddress == null) {
			jTextFieldSipProxyAddress = new JTextField();
		    jTextFieldSipProxyAddress.setText(SIPPROXY_ADDRESS);
		}
		return jTextFieldSipProxyAddress;
	}
	
	/**
	 * This method initializes jTextFieldPuertoProxySip	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldSipProxyPort() {
		if (jTextFieldSipProxyPort == null) {
			jTextFieldSipProxyPort = new JTextField();
			jTextFieldSipProxyPort.setText("5060");
		}
		return jTextFieldSipProxyPort;
	}
	
	/**
	 * This method initializes jButtonAceptar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAceptar() {
		if (jButtonAceptar == null) {
			jButtonAceptar = new JButton();
			jButtonAceptar.setBounds(new java.awt.Rectangle(105,305,101,26));
			jButtonAceptar.setIcon(new ImageIcon(getClass().getResource("/img/complete_task.gif")));
			jButtonAceptar.setText("Aceptar");
			jButtonAceptar.addActionListener(this);
		}
		return jButtonAceptar;
	}
	
	/**
	 * This method initializes jButtonCancelar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancelar() {
		if (jButtonCancelar == null) {
			jButtonCancelar = new JButton();
			jButtonCancelar.setBounds(new java.awt.Rectangle(215,305,111,26));
			jButtonCancelar.setIcon(new ImageIcon(getClass().getResource("/img/error_tsk.gif")));
			jButtonCancelar.setText("Cancelar");
			jButtonCancelar.addActionListener(this);
		}
		return jButtonCancelar;
	}
	
	/**
	 * Este metodo inicializa un objeto jComboBoxIpLocal
	 * ademas escanea las interfaces de red y llena este objeto
	 * con las direcciones correspondientes	
	 *
	 *@return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxLocalHostAddress() {
		
		Enumeration <InetAddress> addresses = null; 
		
		if (jComboBoxLocalHostAddress == null) {
			
			jComboBoxLocalHostAddress = new JComboBox();
			
			try {
				interfaces = NetworkInterface.getNetworkInterfaces();
				
				while (interfaces.hasMoreElements()){
					addresses = interfaces.nextElement().getInetAddresses();
					while (addresses.hasMoreElements()){
						jComboBoxLocalHostAddress.addItem(addresses.nextElement());
					}
				}
				
			} catch (SocketException e) {
				Loguer.showMessageError("No existen interfaces de red para configurar");
			}
		}
		return jComboBoxLocalHostAddress;
	}
	
	
	
	public void actionPerformed(ActionEvent boton) {
		
		if(boton.getActionCommand().compareTo("Aceptar") == 0){
			try {
				createFileXmlConf();
			} catch (ExcGeneric e) {
				Loguer.showMessageError(e.getMessage());
			}
			getJFrameConfig().setVisible(false);
		}
		else if(boton.getActionCommand().compareTo("Cancelar") == 0){
			getJFrameConfig().setVisible(false);
		}
		
	}
	private void writeFile(String file,String data){
		try { 
			
			PrintWriter write = new PrintWriter(
					new BufferedWriter(new FileWriter(file)));
			
			write.println(data);
			write.close();
		} catch (EOFException ex) { 
			System.out.println("Final de Stream");
		}catch (IOException ex) { 
			System.out.println("Final de Stream");
		}
	}
	
	/**
	 * Este metodo genera un archivo confi.xml, el cual tiene informacion
	 * acerca de el servidor stun, y el proxy sip donde hay que conectarse
	 * @return
	 * @throws ExcGeneric
	 */
	private String createFileXmlConf() throws ExcGeneric{
		
		String xml;
		String stunServerAddress;
		String localHostAddress; 
		String sipProxyAddress;
		
		int stunServerPort;
		int localHostPort;
		int sipProxyPort;
		
		if(getJTextFieldStunServerAddress().getText().compareTo("") == 0 ){
			stunServerAddress = STUN_ADDRESS;
		}else{
			stunServerAddress = getJTextFieldStunServerAddress().getText();
		}
		if(getJTextFieldStunServerPort().getText().compareTo("") == 0 ){
			stunServerPort = STUN_PORT;
		}else{
			stunServerPort = Integer.parseInt(getJTextFieldStunServerPort().getText());
		}
		if(getJTextFieldLocalHostPort().getText().compareTo("") == 0 ){
			localHostPort = LOCALHOST_PORT;
		}else{
			localHostPort = Integer.parseInt(getJTextFieldLocalHostPort().getText());
		}
		if(((InetAddress)(getJComboBoxLocalHostAddress().getSelectedItem())).getHostAddress().compareTo("") == 0 ){
			localHostAddress = LOCALHOST_ADDRESS;
		}else{
			localHostAddress = ((InetAddress)getJComboBoxLocalHostAddress().getSelectedItem()).getHostAddress();
		}
		if(getJTextFieldSipProxyAddress().getText().compareTo("") == 0 ){
			sipProxyAddress = SIPPROXY_ADDRESS;
		}else{
			sipProxyAddress = getJTextFieldSipProxyAddress().getText();
		}
		
		if(getJTextFieldSipProxyAddress().getText().compareTo("") == 0 ){
			sipProxyPort = SIPPROXY_PORT;
		}else{
			sipProxyPort = Integer.parseInt(getJTextFieldSipProxyPort().getText());
		}
		xml = "<config>"; 
		xml += "<stunserver>"; 
		xml += "<address>" + stunServerAddress + "</address>";
		xml += "<port>" + stunServerPort + "</port>";
		xml += "</stunserver>";
		xml += "<localhost>";
		xml += "<address>" + localHostAddress +"</address>";
		xml += "<port>" +localHostPort + "</port>";
		xml += "</localhost>";
		xml += "<sipproxy>";
		xml += "<address>" +sipProxyAddress+ "</address>";
		xml += "<port>" + sipProxyPort +"</port>";
		xml += "</sipproxy>";
		
		xml +="</config>";
		
		writeFile(FILE_CONFIG,xml);
		
		return xml;
	}
}
