/**
 * Realiza la lectura de un archivo de configuracion, en el cual
 * se escpecifican diferentes datos para el correcto funcionamiento.
 * Esta clase utiliza un api diferente a la que utiliza la clase XmlCamParser
 */
package consola;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XmlConfigParser{

	String fileXML = null;	
	String stunServerAddress = null;
	String localHostAddress = null;
	String sipProxyAddress = null;
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
				}else if(element.getName().compareTo("localhost") == 0) {
					setLocalHostAddress(element.getChild("address").getText());
					setLocalHostPort(Integer.parseInt(element.getChild("port").getText()));
				}else if(element.getName().compareTo("sipproxy") == 0) {
					setSipProxyAddress(element.getChild("address").getText());
					setSipProxyPort(Integer.parseInt(element.getChild("port").getText()));
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

}

