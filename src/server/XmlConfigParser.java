package server;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
/**
 *  Esta clase utiliza JDOM e interpreta un archivo xml el cual contiene
 *   la configuracion necesaria para la conexion del servidor.
 * @author ivan
 *
 */

public class XmlConfigParser {

/**
 * Contiene el archivo xml a parsear.
 */
    private String fileXML = null;
/**
 * Contiene la direccion del servidor STUN.
 */

	private String stunServerAddress = null;
	
	private String localHostAddress = null;
	
	private String sipProxyAddress = null;
	
	private String dispositivo = null;
	private String usuario = null;
	private String password = null;
	private String tipoCompresion = null;
	private String factorCompresion = null;
	

	int stunServerPort = 0;
	int localHostPort = 0;
	int sipProxyPort = 0;
	
	public XmlConfigParser(String fileName){
		setFileXML(fileName);
	}	
	
	public void parse() throws ExcGeneric{
		
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			org.jdom.Document jdomDocument = saxBuilder.build(getFileXML());
						
			Element domElement = jdomDocument.getRootElement();
			List child = domElement.getChildren();
			Iterator iterator = child.iterator();
			Element element;
			
			while(iterator.hasNext()) {
				element = (Element) iterator.next();

				if(element.getName().compareTo("stunserver") == 0) {
					setStunServerAddress(element.getChild("address").getText());
					setStunServerPort(Integer.parseInt(element.getChild("port").getText()));
				}
				else if(element.getName().compareTo("localhost") == 0) {
					setLocalHostAddress(element.getChild("address").getText());
					setLocalHostPort(Integer.parseInt(element.getChild("port").getText()));
				}
				else if(element.getName().compareTo("sipproxy") == 0) {
					setSipProxyAddress(element.getChild("address").getText());
					setSipProxyPort(Integer.parseInt(element.getChild("port").getText()));
				}
				else if(element.getName().compareTo("dispositivo") == 0) {
					setDispositivo(element.getChild("address").getText());
					setTipoCompresion(element.getChild("compresion").getText());
					setFactorCompresion(element.getChild("factor").getText());
				}
				else if (element.getName().compareTo("usuario") == 0) {
					setUsuario(element.getChild("nombre").getText());
					setPassword(Cifrador.desencriptar("per0r1el",new String(element.getChild("password").getText())));
				}
			}
		} catch (JDOMException e) { // indica doc mal formado u otro error
			throw new ExcGeneric("Documento XML mal formado o incorrecto.");
			
		} catch (IOException e) {
			throw new ExcGeneric(e.getMessage());
		}
		
	}
	
	public String getFileXML() {
		return fileXML;
	}
	public void setFileXML(String fileXML) {
		this.fileXML = fileXML;
	}

	public String getLocalHostAddress() {
		return localHostAddress;
	}

	private void setLocalHostAddress(String localHostAddress) {
		this.localHostAddress = localHostAddress;
	}

	public int getLocalHostPort() {
		return localHostPort;
	}

	private void setLocalHostPort(int localHostPort) {
		this.localHostPort = localHostPort;
	}

	public String getSipProxyAddress() {
		return sipProxyAddress;
	}

	private void setSipProxyAddress(String sipProxyAddress) {
		this.sipProxyAddress = sipProxyAddress;
	}

	public int getSipProxyPort() {
		return sipProxyPort;
	}

	private void setSipProxyPort(int sipProxyPort) {
		this.sipProxyPort = sipProxyPort;
	}

	public String getStunServerAddress() {
		return stunServerAddress;
	}

	private void setStunServerAddress(String stunServerAddress) {
		this.stunServerAddress = stunServerAddress;
	}

	public int getStunServerPort() {
		return stunServerPort;
	}

	private void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
	}

	public String getDispositivo() {
		return dispositivo;
	}

	private void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}

	public String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	public String getUsuario() {
		return usuario;
	}

	private void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getFactorCompresion() {
		return factorCompresion;
	}

	public String getTipoCompresion() {
		return tipoCompresion;
	}

	public void setFactorCompresion(String factorCompresion) {
		this.factorCompresion = factorCompresion;
	}

	public void setTipoCompresion(String tipoCompresion) {
		this.tipoCompresion = tipoCompresion;
	}

}

