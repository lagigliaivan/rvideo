package client;
/**
 * Esta clase es la ventana principal que contendra diferentes objetos.
 * Esta ventana es creada por la fc. main, y muestra por ej las text de loguin y pass
 *
 * @author lagiglia ivan
 * @version 1.0 
 *  
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;



public class FrmPrincipal implements ActionListener,KeyListener{

	private int x = 0;
	private int y = 0;
	
	private ThrDialogRegister dialog = null;
	private String fileXmlCamaras = null;
	private JButtonConnect jButtonConnect = null;
	private JButtonConfigure jButtonConfigurar = null;
	private JPanel jContentPaneVentanaPrincipal = null;
	private JFrame jFrameVentanaPrincipal = null;
	private JLabel jLabelPass = null;
	private JLabel jLabelUserName = null;
	private JMenu jMenuAyuda = null;
	private JMenuBar jMenuBarVentanaPrincipal = null;
	private JMenu jMenuCamaras = null;
	private JMenuItemAbout jMenuItemAbout = null;
	private JMenuItemAddCamera jMenuItemAgregarCamara = null;
	private JMenuItemDeleteCamera jMenuItemEliminar = null;
	private JMenuItemExit jMenuItemSalir = null;
	private JPanel jPanelAceptar = null;
	private FrmListaCamaras jPanelListaCamaras = null;
	private JPanel jPanelNameAndPass = null;
	private JPasswordField jPasswordFieldPass = null;
	private JTextField jTextFieldNombreUsuario = null;
	private SipManager sipManager = null;
	private XmlCamParser xmlParser = new XmlCamParser();
	
	/**
	 * @return the xmlParser
	 */
	public XmlCamParser getXmlParser() {
		return xmlParser;
	}

	/**
	 * @param xmlParser the xmlParser to set
	 */
	public void setXmlParser(XmlCamParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	private XmlConfigParser xmlConfigParser = null;
	private JMenuItemRefreshList jMenuItemRefresh = null;
	public Logger logger = Logger.getLogger("FrmPrincipal");
	
	/**
	 * This method is executed every time the button jButtonAcetpar is pressed or some of the menus in the bar or popup is selected.
	 */
	public void actionPerformed(ActionEvent event){
	
		//TODO avoid hardcoded file names.  In this case listaCamaras.xml.
		fileXmlCamaras  = System.getProperty("user.dir") + File.separator + getJTextFieldNombreUsuario().getText() + File.separator  + "listaCamaras.xml";

		/**
		 *	Click on the "Conectarse" button 
		 */
		try {
			((ActionButton)(event.getSource())).performAction(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the jPanelListaCamaras
	 */
	public FrmListaCamaras getJPanelListaCamaras() {
		return jPanelListaCamaras;
	}

	/**
	 * @param panelListaCamaras the jPanelListaCamaras to set
	 */
	public void setJPanelListaCamaras(FrmListaCamaras panelListaCamaras) {
		jPanelListaCamaras = panelListaCamaras;
	}

	/**
	 * @return the fileXmlCamaras
	 */
	public String getFileXmlCamaras() {
		return fileXmlCamaras;
	}

	/**
	 * @param fileXmlCamaras the fileXmlCamaras to set
	 */
	public void setFileXmlCamaras(String fileXmlCamaras) {
		this.fileXmlCamaras = fileXmlCamaras;
	}

	/**
	 * Este metodo inicializa el jButtonAceptar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAceptar() {
		if (jButtonConnect == null) {
			jButtonConnect = new JButtonConnect();
			jButtonConnect.setText("Conectar");
			jButtonConnect.setIcon(new ImageIcon(getClass().getResource("img" + File.separator + "plugin_obj.gif")));
			jButtonConnect.setToolTipText("Click aqui para autenticarse con el registrar SIP");
			/**
			 * Cuando se presione este boton, la clase que tendra el metodo que 
			 * atender dicha accion sera FrmPrincipal
			 */
			jButtonConnect.addActionListener(this);
		}
		return jButtonConnect;
	}

	/**
	 * This method initializes jButtonConfigurar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonConfigurar() {
		if (jButtonConfigurar == null) {
			jButtonConfigurar = new JButtonConfigure();
			jButtonConfigurar.setText("Configurar");
			jButtonConfigurar.setIcon(new ImageIcon(getClass().getResource("img" + File.separator + "editorpane_obj.gif")));
			jButtonConfigurar.addActionListener(this);
		}
		return jButtonConfigurar;
	}

	/**
	 * Este metodo inicializa jContentPaneVentanaPrincipal
	 * El jContentPaneVentanaPrincipal es el contenedor de jFrameVentanaPrincipal
	 * que lleva dentro dos contenedores mas. Uno de ellos contiene dos text (user y pass)
	 * y el otro contiene un bot�n de conexion
	 * 	
	 *  @return javax.swing.JPanel	
	 */

	public JPanel getJContentPaneVentanaPrincipal() {
		if (jContentPaneVentanaPrincipal == null) {
			jContentPaneVentanaPrincipal = new JPanel();
			jContentPaneVentanaPrincipal.setLayout(new BorderLayout());
			jContentPaneVentanaPrincipal.add(getJPanelAceptar(), java.awt.BorderLayout.SOUTH);
			jContentPaneVentanaPrincipal.add(getJPanelNameAndPass(), java.awt.BorderLayout.CENTER);
		}
		return jContentPaneVentanaPrincipal;
	}

	/**
	 * Este metodo inicializa jFrameVentanaPrincipal	
	 * @return javax.swing.JFrame	
	 */
	public JFrame getJFrameVentanaPrincipal() {

		if (jFrameVentanaPrincipal == null) {

			/**
			 * Obtiene el tama�o de la pantalla
			 * y lo utiliza para mostrar la ventana inicial
			 * en el centro de la misma
			 */

			x = Tools.getCenterPosition().x;
			y = Tools.getCenterPosition().y;

			jFrameVentanaPrincipal = new JFrame();
			jFrameVentanaPrincipal.setSize(new java.awt.Dimension(256,397));
			jFrameVentanaPrincipal.setTitle("Consola");
			jFrameVentanaPrincipal.setMaximumSize(new java.awt.Dimension(262,409));
			jFrameVentanaPrincipal.setMinimumSize(new java.awt.Dimension(262,409));
			jFrameVentanaPrincipal.setResizable(false);
			jFrameVentanaPrincipal.setPreferredSize(new java.awt.Dimension(262,409));
			jFrameVentanaPrincipal.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img" + File.separator + "system.gif")));
			jFrameVentanaPrincipal.setContentPane(getJContentPaneVentanaPrincipal());
			jFrameVentanaPrincipal.setJMenuBar(getJMenuBarVentanaPrincipal());
			jFrameVentanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrameVentanaPrincipal.setLocation(x/2, y/5);		
		}

		return jFrameVentanaPrincipal;
	}

	/**
	 * Este metodo inicializa el menu  jMenuAyuda	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuAyuda() {
		if (jMenuAyuda == null) {
			jMenuAyuda = new JMenu();
			jMenuAyuda.setText("Ayuda");
			jMenuAyuda.add(getJMenuItemAbout());
		}
		return jMenuAyuda;
	}
	/**
	 * Este metodo inicializa la barra de menues jMenuBarVentanaPrincipal	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	public JMenuBar getJMenuBarVentanaPrincipal() {
		if (jMenuBarVentanaPrincipal == null) {
			jMenuBarVentanaPrincipal = new JMenuBar();
			jMenuBarVentanaPrincipal.setVisible(false);
			jMenuBarVentanaPrincipal.add(getJMenuCamaras());
			jMenuBarVentanaPrincipal.add(getJMenuAyuda());
		}
		return jMenuBarVentanaPrincipal;
	}

	/**
	 * Este metodo inicializa el menu jMenuCamaras	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuCamaras() {
		if (jMenuCamaras == null) {
			jMenuCamaras = new JMenu();
			jMenuCamaras.setText("Camaras");
			jMenuCamaras.add(getJMenuItemAgregarCamara());
			jMenuCamaras.add(getJMenuItemEliminar());
			jMenuCamaras.add(getJMenuItemRefresh());
			jMenuCamaras.addSeparator();
			jMenuCamaras.add(getJMenuItemSalir());
		}
		return jMenuCamaras;
	}

	/**
	 * This method initializes jMenuItemAbout	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemAbout() {
		if (jMenuItemAbout == null) {
			jMenuItemAbout = new JMenuItemAbout();
			jMenuItemAbout.setText("Acerca de");
			jMenuItemAbout.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemAbout.addActionListener(this);
		}
		return jMenuItemAbout;
	}


	/**
	 * Este metodo inicializa el menu jMenuItemAgregarCamara	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemAgregarCamara() {
		if (jMenuItemAgregarCamara == null) {
			jMenuItemAgregarCamara = new JMenuItemAddCamera();
			jMenuItemAgregarCamara.setText("Agregar camara");
			jMenuItemAgregarCamara.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemAgregarCamara.addActionListener(this);
		}
		return jMenuItemAgregarCamara;
	}

	/**
	 * Este metodo inicializa el menu  jMenuItemEliminar	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemEliminar() {
		if (jMenuItemEliminar == null) {
			jMenuItemEliminar = new JMenuItemDeleteCamera();
			jMenuItemEliminar.setText("Eliminar Camara");
			jMenuItemEliminar.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemEliminar.addActionListener(this);
		}
		return jMenuItemEliminar;
	}

	/**
	 * Este metodo inicializa el menu jMenuItemSalir	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemSalir() {
		if (jMenuItemSalir == null) {
			jMenuItemSalir = new JMenuItemExit();
			jMenuItemSalir.setText("Salir");
			jMenuItemSalir.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemSalir.addActionListener(this);
		}
		return jMenuItemSalir;
	}

	/**
	 * Este metodo inicializa jPanelAceptar, que es un contenedor que esta dentro de
	 * 	jContentPaneVentanaPrincipal y contiene el bonton de conexion
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelAceptar() {
		if (jPanelAceptar == null) {
			jPanelAceptar = new JPanel();
			jPanelAceptar.setBorder(javax.swing.BorderFactory.createMatteBorder(2,0,0,0,java.awt.Color.gray));
			jPanelAceptar.add(getJButtonAceptar(), null);
			jPanelAceptar.add(getJButtonConfigurar(), null);
		}
		return jPanelAceptar;
	}

	/**
	 * Este metodo inicializa el jPanelNameAndPass
	 * Este contenedor esta dentro de jContentPaneVentanaPrincipal y contiene las
	 * text de user y pass.	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelNameAndPass() {
		if (jPanelNameAndPass == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints3.gridy = 4;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.insets = new java.awt.Insets(5,0,2,0);
			gridBagConstraints2.gridy = 3;
			jLabelPass = new JLabel();
			jLabelPass.setText("Contrasenia");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.insets = new java.awt.Insets(5,0,2,0);
			gridBagConstraints.gridy = 1;
			jLabelUserName = new JLabel();
			jLabelUserName.setText("Nombre de Usuario");
			jPanelNameAndPass = new JPanel();
			jPanelNameAndPass.setLayout(new GridBagLayout());
			jPanelNameAndPass.add(jLabelUserName, gridBagConstraints);
			jPanelNameAndPass.add(getJTextFieldNombreUsuario(), gridBagConstraints1);
			jPanelNameAndPass.add(jLabelPass, gridBagConstraints2);
			jPanelNameAndPass.add(getJPasswordFieldPass(), gridBagConstraints3);
		}
		return jPanelNameAndPass;
	}

	/**
	 * Este metodo inicializa jPasswordFieldPass	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordFieldPass() {
		if (jPasswordFieldPass == null) {
			jPasswordFieldPass = new JPasswordField();
			jPasswordFieldPass.setPreferredSize(new java.awt.Dimension(200,20));
			jPasswordFieldPass.setHorizontalAlignment(javax.swing.JTextField.CENTER);
			jPasswordFieldPass.addKeyListener(this);
		}
		return jPasswordFieldPass;
	}

	/**
	 * Este metodo inicializa jTextFieldNombreUsuario	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldNombreUsuario() {
		if (jTextFieldNombreUsuario == null) {
			jTextFieldNombreUsuario = new JTextField();
			jTextFieldNombreUsuario.setPreferredSize(new java.awt.Dimension(200,20));
			jTextFieldNombreUsuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);
			jTextFieldNombreUsuario.setToolTipText("Nombre de usuario con la forma nombre@dominio");
			jTextFieldNombreUsuario.addKeyListener(this);
		}
		return jTextFieldNombreUsuario;
	}


	public SipManager getSipManager() {
		return sipManager;
	}
	public void keyPressed(KeyEvent key) {}

	public void keyReleased(KeyEvent key) {}

	/**
	 * este metodo se ejecuta cuando se presiona enter
	 * sobre la text del user o del pass
	 */
	public void keyTyped(KeyEvent key) {
		if(key.getKeyChar()=='\n')
			validateAndConnect();
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}

	public void showList(){
		if(jPanelListaCamaras==null)
			jPanelListaCamaras = new FrmListaCamaras(sipManager,this);

		getJContentPaneVentanaPrincipal().removeAll();
		getJContentPaneVentanaPrincipal().add(jPanelListaCamaras.getJPanelListaCamaras(),java.awt.BorderLayout.CENTER);
		getJContentPaneVentanaPrincipal().setVisible(true);
		getJContentPaneVentanaPrincipal().repaint();
		getJFrameVentanaPrincipal().setVisible(true);
		getJFrameVentanaPrincipal().repaint();

	}

	/**
	 * Este metod valida el formato del loguin y el pass, luego valida
	 * que exista el archivo xml de configuracion. Ademas se registra 
	 * con el Registrar SIP
	 *
	 */
	public void validateAndConnect(){

		File fileConfig = new File(System.getProperty("user.dir") +  File.separator + "config.xml");

		if(!fileConfig.exists()){
			Loguer.showMessageError("Primero debe configurar la conexion");
			return;
		}else {
			setXmlConfigParser(new XmlConfigParser(fileConfig.getAbsolutePath()));
			try {
				getXmlConfigParser().parse();
			} catch (ExcGeneric e) {
				Loguer.showMessageError(e.getMessage());
				return;
			}
		}

		char[] pass = getJPasswordFieldPass().getPassword();

		if(pass.length == 0){
			Loguer.showMessageError("Ingrese Password");
			return;
		}

		/**
		 * instancio Objeto SipManager que va a manejar todas las conexiones
		 * salientes y entrantes
		 */

		if(sipManager == null){

			try {	
				sipManager = new SipManager(Constants.PROTOCOL, getXmlConfigParser().getLocalHostPort(), Constants.STACK_PATH_NAME, Constants.STACK_PATH);
			}
			catch(UnknownHostException unknownHostExc){
				Loguer.showMessageError("No se pudo habrir tener acceso a la red");
				sipManager = null;
				getJButtonAceptar().setEnabled(true);
				return;
			}
			sipManager.setOutboundProxy(xmlConfigParser.getSipProxyAddress());
			sipManager.setStackAddress(xmlConfigParser.getLocalHostAddress());
			sipManager.setStunServer(xmlConfigParser.getStunServerAddress());
			sipManager.setStunServerPort(xmlConfigParser.getStunServerPort());

		}else{
			sipManager.reset();
		}
		/**
		 * Instancio un JDialog que va a utilizar al objeto 
		 * SipManager para registrarse
		 */	
				
		dialog = new ThrDialogRegister(sipManager,this);
		dialog.setNombreUsuario(getJTextFieldNombreUsuario().getText().trim());
		dialog.setDominio(xmlConfigParser.getSipProxyAddress());
		dialog.setPass(getJPasswordFieldPass().getPassword());
		//Lo situo en medio de la ventana principal
		dialog.setX(x/2 - 100);
		dialog.setY(y/5 + 100);
		dialog.start();
	}

	public void writeFileXml(String file,String data){
		try { 
			PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			write.println(data);
			write.close();
		} catch (EOFException ex) { 
			System.out.println("Final de Stream");
		}catch (IOException ex) { 
			System.out.println("Final de Stream");
		}
	}

	private XmlConfigParser getXmlConfigParser() {
		return xmlConfigParser;
	}

	private void setXmlConfigParser(XmlConfigParser xmlConfigParser) {
		this.xmlConfigParser = xmlConfigParser;
	}

	/**
	 * This method initializes jMenuItemRefresh	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemRefresh() {
		if (jMenuItemRefresh == null) {
			jMenuItemRefresh = new JMenuItemRefreshList();
			jMenuItemRefresh.setText("Refrescar lista");
			jMenuItemRefresh.setFont(new Font("Dialog", Font.PLAIN, 12));
			jMenuItemRefresh.addActionListener(this);
		}

		return jMenuItemRefresh;
	}

}