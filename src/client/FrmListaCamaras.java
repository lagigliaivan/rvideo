package consola;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
/**
 * Esta clase visualiza la lista de camaras disponibles para el usuario
 * logueado.  Al presionar el boton visualizar o hacer doble click sobre
 * la camara seleccionada se dispara un hilo, el cual se conectara con
 * la camara.
 * @author ivan
 *
 */

public class FrmListaCamaras implements MouseListener,ActionListener {

	private JPanel jPanelListaCamaras = null;  //  @jve:decl-index=0:visual-constraint="141,22"
	private JPanel jPanelCamaras = null;
	private JPanel jPanelVisualizar = null;
	private JButton jButtonVisualizar = null;
	private JList jListCamaras = null;
	private SipManager sipManager;
	private JList list;
	private Vector camaras;
	private FrmPrincipal parent = null;
	private XmlCamParser xmlParser = new XmlCamParser();
	private String fileXmlCamaras = null;
	
	private GraphicsDevice gD = null;
	private DisplayMode	dM = null;
	int x = 0;
	int y = 0;
	GraphicsEnvironment gE = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private JScrollPane jScrollPaneListaCamaras = null;
	private JPopupMenu jPopupMenuCamaras = null; 
	private JMenuItem jMenuItemEliminarCamara = null;
	private JMenuItem jMenuItemAgregarCamara = null;
	private JMenuItem jMenuItemPropiedadesCamara = null;
	private JMenuItem jMenuItemAgregarModifProps = null;
	/**
	 * This method initializes jPanelListaCamaras	
	 * 
	 */
	public FrmListaCamaras(SipManager manager,FrmPrincipal parent){
		super();
		setParent(parent);
		sipManager = manager;
		
		//instancio un objeto directorio
		File directorio = new File( System.getProperty("user.dir") + File.separator + sipManager.getUserName() );
		
		//lo creo fisicamente en el filesystem
		if(!directorio.exists()){
			directorio.mkdir();
		}	
		File fileCamaras = new File( System.getProperty("user.dir") + File.separator + sipManager.getUserName()+ File.separator +"listaCamaras.xml");
		
		if(!fileCamaras.exists())
			try {
				fileCamaras.createNewFile();
			}catch (IOException ioException){ 
				Loguer.showMessageError("Error al crear archivo con la lista de camaras disponibles \n " +
									     "El error devuelto es: " + ioException.getMessage());
								
				System.exit(1);
			}
		
		fileXmlCamaras = System.getProperty("user.dir") + File.separator + sipManager.getUserName() + File.separator + "listaCamaras.xml";
	}
	public JPanel getJPanelListaCamaras() {
		if (jPanelListaCamaras == null) {
			gD = gE.getDefaultScreenDevice();
			dM = gD.getDisplayMode();
			x = dM.getHeight();
			y = dM.getWidth();
			jPanelListaCamaras = new JPanel();
			jPanelListaCamaras.setLayout(new BorderLayout());
			jPanelListaCamaras.setSize(new java.awt.Dimension(262,409));
			jPanelListaCamaras.setPreferredSize(new java.awt.Dimension(262,409));
			jPanelListaCamaras.setMinimumSize(new java.awt.Dimension(262,409));
			jPanelListaCamaras.setMaximumSize(new java.awt.Dimension(262,409));
			jPanelListaCamaras.add(getJPanelVisualizar(), java.awt.BorderLayout.SOUTH);
			jPanelListaCamaras.add(getJPanelCamaras(), java.awt.BorderLayout.CENTER);
		}else{
			jPanelListaCamaras.removeAll();
			jPanelListaCamaras.add(getJPanelVisualizar(), java.awt.BorderLayout.SOUTH);
			jPanelListaCamaras.add(getJPanelCamaras(), java.awt.BorderLayout.CENTER);
		}
		return jPanelListaCamaras;
	}
	
	/**
	 * This method initializes jPanelCamaras	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelCamaras() {
		if (jPanelCamaras == null) {
			jPanelCamaras = new JPanel();
			jPanelCamaras.setLayout(new BorderLayout());
			jPanelCamaras.add(getJScrollPaneListaCamaras(), java.awt.BorderLayout.CENTER);
		}else{
			jPanelCamaras.removeAll();
			jPanelCamaras.add(getJScrollPaneListaCamaras(), java.awt.BorderLayout.CENTER);
		}
		return jPanelCamaras;
	}

	/**
	 * This method initializes jPanelVisualizar	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelVisualizar() {
		if (jPanelVisualizar == null) {
			jPanelVisualizar = new JPanel();
			jPanelVisualizar.setBorder(javax.swing.BorderFactory.createMatteBorder(2,0,0,0,java.awt.Color.gray));
			jPanelVisualizar.add(getJButtonVisualizar(), null);
		}
		return jPanelVisualizar;
	}

	/**
	 * This method initializes jButtonVisualizar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonVisualizar() {
		if (jButtonVisualizar == null) {
			jButtonVisualizar = new JButton();
			jButtonVisualizar.setText("Visualizar");
			jButtonVisualizar.setIcon(new ImageIcon(getClass().getResource("/img/run.gif")));
			jButtonVisualizar.addActionListener(this);
		}
		return jButtonVisualizar;
	}

	/**
	 * This method initializes jListCamaras	
	 * 	
	 * @return javax.swing.JList	
	 */
	public JList getJListCamaras() {
		
		camaras = xmlParser.getCamaras(fileXmlCamaras);
	//	String data[] = new String[camaras.size()];
		Iterator iterator = camaras.iterator();
		String cam = null;
		String user = null;
		String domain = null;
		int sipCon = 0;
		DefaultListModel model = new DefaultListModel();
		ListItem li = null;
		
		while(iterator.hasNext()){
			cam = (String)iterator.next();
			user = cam.substring(0, cam.indexOf('@'));
			domain = cam.substring(cam.indexOf('@') + 1);
		    
			try {
				 sipCon = getSipManager().createMessageSipConexion(user,domain);
			} catch (ExcGeneric e) {
				
			}
			try {
				getSipManager().sendMessageRequest(sipCon);
				li = new ListItem(Color.white, cam);
				li.setIcon(new ImageIcon(getClass().getResource("/img/connected.gif")));
 
		        			
			} catch (ExcGeneric e) {
				li = new ListItem(Color.LIGHT_GRAY, cam);
				li.setIcon(new ImageIcon(getClass().getResource("/img/delete.gif")));
		    }
			model.addElement(li);
		}

		
		if (jListCamaras == null) {
			jListCamaras = new JList(model);
			jListCamaras.setCellRenderer(new MyCellRenderer());
			jListCamaras.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
			jListCamaras.addMouseListener(this);
		
		}else{
			jListCamaras.clearSelection();
		    jListCamaras.setModel(model);
			jListCamaras.setEnabled(true);
			jListCamaras.repaint();
		}
		return jListCamaras;
	}
	
	public JList getJListCamarasRaw(){
		return jListCamaras;
	}
	public void mouseClicked(MouseEvent e) {
		
		this.list = (JList)e.getSource();
		
		if(e.getClickCount()== 2 && e.getButton() == 1){
			
			ThrDialogInviter dialogInvite = new ThrDialogInviter(sipManager,getParent());
			ListItem a = (ListItem)list.getModel().getElementAt(list.getSelectedIndex());
			dialogInvite.setNombreUsuario(a.getValue());
			dialogInvite.setX(x/2 - 100);
			dialogInvite.setY(y/5 + 100);
			dialogInvite.start();

		}
		if(e.getButton()== 3 && list.getSelectedIndex()!= -1 ){
			
			getJPopupMenuCamaras().show(list, e.getX(), e.getY());
		
		}
	}
	
	public JList getList() {
		return list;
	}
	public void setList(JList list) {
		this.list = list;
	}
	

	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {

	}
	/**
	 * This method initializes jScrollPaneListaCamaras	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneListaCamaras() {
		if (jScrollPaneListaCamaras == null) 
			jScrollPaneListaCamaras = new JScrollPane();
				
		jScrollPaneListaCamaras.setViewportView(getJListCamaras());
				
		return jScrollPaneListaCamaras;
	}
	/**
	 * This method initializes jPopupMenuCamaras	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getJPopupMenuCamaras() {
		if (jPopupMenuCamaras == null) {
			jPopupMenuCamaras = new JPopupMenu();
			jPopupMenuCamaras.add(getJMenuItemEliminarCamara());
			jPopupMenuCamaras.add(getJMenuItemAgregarCamara());
			jPopupMenuCamaras.add(getJMenuItemPropiedadesCamara());
			jPopupMenuCamaras.add(getJMenuItemAgregarMidifProps());
			
		}
		return jPopupMenuCamaras;
	}
	/**
	 * This method initializes jMenuItemEliminarCamara	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemEliminarCamara() {
		if (jMenuItemEliminarCamara == null) {
			jMenuItemEliminarCamara = new JMenuItem();
			jMenuItemEliminarCamara.setText("Eliminar");
			jMenuItemEliminarCamara.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemEliminarCamara.addActionListener(getParent());
		}
		return jMenuItemEliminarCamara;
	}
	public FrmPrincipal getParent() {
		return parent;
	}
	public void setParent(FrmPrincipal parent) {
		this.parent = parent;
	}
	/**
	 * This method initializes jMenuItemAgregar	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemAgregarCamara() {
		if (jMenuItemAgregarCamara == null) {
			jMenuItemAgregarCamara = new JMenuItem();
			jMenuItemAgregarCamara.setText("Nueva camara");
			jMenuItemAgregarCamara.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemAgregarCamara.addActionListener(getParent());
		}
		return jMenuItemAgregarCamara;
	}
	private JMenuItem getJMenuItemPropiedadesCamara() {
		if (jMenuItemPropiedadesCamara == null) {
			jMenuItemPropiedadesCamara = new JMenuItem();
			jMenuItemPropiedadesCamara.setText("Ver Propiedades");
			jMenuItemPropiedadesCamara.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemPropiedadesCamara.addActionListener(getParent());
		}
		return jMenuItemPropiedadesCamara;
	}	
	private JMenuItem getJMenuItemAgregarMidifProps() {
		if (jMenuItemAgregarModifProps == null) {
			jMenuItemAgregarModifProps = new JMenuItem();
			jMenuItemAgregarModifProps.setText("Modif Propiedades");
			jMenuItemAgregarModifProps.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			jMenuItemAgregarModifProps.addActionListener(getParent());
		}
		return jMenuItemAgregarModifProps;
	}
	/**
	 * Este metodo atiende el evento generado al precionar el boton
	 * visualizar
	 */
	public void actionPerformed(ActionEvent arg0) {
		if(list.getSelectedIndex()<0)
			return;
		ThrDialogInviter dialogInvite = new ThrDialogInviter(sipManager,getParent());
		ListItem item = (ListItem) list.getModel().getElementAt(list.getSelectedIndex());
		dialogInvite.setNombreUsuario(item.getValue());
		dialogInvite.setX(x/2 - 100);
		dialogInvite.setY(y/5 + 100);
		dialogInvite.start();
	}
	public SipManager getSipManager() {
		return sipManager;
	}
	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}	
}

/**
 * 
 * @author ivan
 *
 */
@SuppressWarnings("serial")
class MyCellRenderer extends JLabel implements ListCellRenderer {

  
public MyCellRenderer () {
       // Don't paint behind the component
       setOpaque(true);
   }

   // Set the attributes of the 
   //class and return a reference
   public Component  getListCellRendererComponent(
		 JList list,
         Object value, // value to display
         int index,    // cell index
         boolean iss,  // is selected
         boolean chf)  // cell has focus?
   {
	   
	    // Set the text and 
        //background color for rendering
        setText(((ListItem)value).getValue());
        setBackground(((ListItem)value).getColor());
        setIcon(((ListItem)value).getIcon());
        // Set a border if the 
         //list item is selected
        if (iss) {
            setBorder(BorderFactory.createLineBorder(
              Color.blue, 2));
        } else {
            setBorder(BorderFactory.createLineBorder(
             list.getBackground(), 2));
        }

        return this;
    }
 }

/**
 * 
 * @author ivan
 *
 */
class ListItem {
    private Color color;
    private String value;
    private ImageIcon icono ;

    public ListItem(
       Color c, String s) {
        color = c;
        value = s;
    }
    public Color getColor() {
        return color;
    }
    public String getValue() {
        return value;
    }
    public void setIcon(ImageIcon icon){
    	icono = icon;
    }
    public ImageIcon getIcon(){
    	return icono;
    }
}
