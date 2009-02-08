/**
 * Esta clase abre un objeto JDialog mostrando una breve descripcion 
 * de una camara listada en la aplicacion
 * 
 * @version 1.0  14 Setiembre 2006
 * @author Ivan  
 */

package client;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class DlgGetDescrip implements ActionListener {

	private JDialog jDialogDesc = null;  
	private JPanel jContentPaneDialog = null;
	private JTextPane jTextPaneDesc = null;
	private JLabel jLabelDesc = null;
	private JPanel jPanelBotonAceptar = null;
	private JButton jButtonAceptar = null;
	private String camara = null;
	private String desc = null;
	private XmlCamParser xmlParser = new XmlCamParser();
	private String fileXml = null;
	private FrmPrincipal parent = null;
    
    
	public DlgGetDescrip(String desc, String camara){
	    setCamara(camara);
		setDesc(desc);
	}
	
	
	/**
	 * This method initializes jDialogDesc	
	 * 	
	 * @return javax.swing.JDialog	
	 */
	public JDialog getJDialogDesc() {
		if (jDialogDesc == null) {
			jDialogDesc = new JDialog();
			jDialogDesc.setSize(new java.awt.Dimension(376,179));
			jDialogDesc.setResizable(false);
			jDialogDesc.setTitle(getCamara());
			jDialogDesc.setContentPane(getJContentPaneDialog());
			
		}
		return jDialogDesc;
	}

	/**
	 * This method initializes jContentPaneDialog	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPaneDialog() {
		if (jContentPaneDialog == null) {
			jLabelDesc = new JLabel();
			jLabelDesc.setText("Coloque en este espacio la descripcion de esta camara:");
			jLabelDesc.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
			jContentPaneDialog = new JPanel();
			jContentPaneDialog.setLayout(new BorderLayout());
			jContentPaneDialog.add(getJTextPaneDesc(), java.awt.BorderLayout.CENTER);
			jContentPaneDialog.add(jLabelDesc, java.awt.BorderLayout.NORTH);
			jContentPaneDialog.add(getJPanelBotonAceptar(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPaneDialog;
	}

	/**
	 * This method initializes jTextPaneDesc	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getJTextPaneDesc() {
		if (jTextPaneDesc == null) {
			jTextPaneDesc = new JTextPane();
			jTextPaneDesc.setText(getDesc());
		}
		return jTextPaneDesc;
	}

	/**
	 * This method initializes jPanelBotonAceptar	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelBotonAceptar() {
		if (jPanelBotonAceptar == null) {
			jPanelBotonAceptar = new JPanel();
			jPanelBotonAceptar.add(getJButtonAceptar(), null);
		}
		return jPanelBotonAceptar;
	}

	/**
	 * This method initializes jButtonAceptar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAceptar() {
		if (jButtonAceptar == null) {
			jButtonAceptar = new JButton();
			jButtonAceptar.setText("Aceptar");
			jButtonAceptar.addActionListener(this);
		}
		return jButtonAceptar;
	}
	
	public String getCamara() {
		return camara;
	}
	
	public void setCamara(String camara) {
		this.camara = camara;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFileXml() {
		return fileXml;
	}


	public void setFileXml(String fileXml) {
		this.fileXml = fileXml;
	}

	public XmlCamParser getXmlParser() {
		return xmlParser;
	}

	public void setXmlParser(XmlCamParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().compareTo("Aceptar") == 0){

			Vector camaras = xmlParser.getCamaras(getFileXml());
			String dataFile = "<camaras>\n";
			
			for(int i = 0 ; i < camaras.size();i++){
				
				String desc = xmlParser.getCamProperties(getFileXml(),(String)camaras.get(i));
				
				if(((String)camaras.get(i)).compareTo((String)getCamara())==0){
					dataFile +="<camara>\n";
					dataFile += "<name>"+(String)getCamara()+"</name>\n";
					dataFile += "<desc>"+getJTextPaneDesc().getText()+"</desc>\n";
					dataFile +="</camara>\n";
				}else{
					
					dataFile +="<camara>\n";
					dataFile += "<name>"+(String)camaras.get(i)+"</name>\n";
					dataFile += "<desc>"+desc+"</desc>\n";
					dataFile +="</camara>\n";
					
				}
				
			}
			dataFile += "</camaras>\n"; 
			getParent().writeFileXml(getFileXml(), dataFile);
			getParent().showList();
			getJDialogDesc().setVisible(false);
			return;
			
		}
		
	}


	public FrmPrincipal getParent() {
		return parent;
	}

	public void setParent(FrmPrincipal parent) {
		this.parent = parent;
	}
}
