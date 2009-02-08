package consola;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XmlCamParser extends DefaultHandler {
	
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser = null;
	XMLReader xmlReader = null;
	Vector <String> vectorCam = new Vector <String> (1,1);
	String qname = "";
	String camProp = "";
	String ultimaCamara = "";
	String descripcion = null;

	public String getUltimaCamara() {
		return ultimaCamara;
	}

	public void setUltimaCamara(String ultimaCamara) {
		this.ultimaCamara = ultimaCamara;
	}

	public String getCamProp() {
		return camProp;
	}

	public void setCamProp(String camProp) {
		this.camProp = camProp;
	}

	public XmlCamParser(){

		try {
			saxParser = factory.newSAXParser();
		  
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	
	}
	
	public String getPropsCamara(String camara,String fileName){
		descripcion = null;
		try {
			getSaxParser().parse(fileName,this);
		} catch (SAXException e) {
			e.printStackTrace();
			descripcion = "";
		} catch (IOException e) {
			e.printStackTrace();
			descripcion = "";
		}
		return descripcion;
	}
	public Vector getCamaras(String fileName){

		
		try {
			vectorCam.removeAllElements();
			vectorCam.clear();
			getSaxParser().parse(fileName,this);
		} catch (SAXException saxEx) {
			System.err.println("lista vacia o no existe");
		} catch (IOException IOEx) {
			System.err.println("lista vacia o no existe");
		}

		return (Vector)(vectorCam.clone());
	}

	
	public void setCamara(String fileName){
				
			try {
				saxParser.parse(fileName,this);
			} catch (SAXException e) {
				System.err.println(e.getMessage());
				System.err.println("error");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}


    public void characters (char ch[], int start, int length) {
    	    	
    	char cam[] = new char[length];

     if (getQname().compareTo("name") == 0) {
    		
    		for (int i = start,j = 0; i < start + length; i++, j++) {
    			cam[j] = ch[i];
    		}
    		setQname("");
    		vectorCam.add(new String(cam));
        	setVectorCam(vectorCam);
        	setUltimaCamara(new String(cam));
        	return;
    	}

    	if(getQname().compareTo("desc")== 0 && getUltimaCamara().compareTo(getCamProp()) == 0){
    		
    		for (int i = start,j=0; i < start + length; i++,j++) {
    			cam[j] = ch[i];
    		}
    		setPropiedades(new String(cam));
    		setUltimaCamara(""); 
    		return;
    	}

    	
    }
	public XMLReader getXmlReader() {
		return xmlReader;
	}

	public void setXmlReader(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
	}

	public Vector getVectorCam() {
		return vectorCam;
	}

	public void setVectorCam(Vector <String> vectorCam) {
		this.vectorCam = vectorCam;
	}

	public SAXParser getSaxParser() {
		return saxParser;
	}

	public void setSaxParser(SAXParser saxParser) {
		this.saxParser = saxParser;
	} 
	 public void startElement(String uri,String localName,String qName,
			  Attributes attributes)throws SAXException{

		  this.qname = qName;
	 }

	public String getQname() {
		return qname;
	}

	public void setQname(String qname) {
		this.qname = qname;
	}

	public String getCamProperties(String fileName,String cam){
		try {
		  setCamProp(cam);
		  getSaxParser().parse(fileName,this);
		} catch (SAXException saxEx) {
			
		} catch (IOException IOEx) {
			System.err.println("lista vacia o no existe");
		}
		return getDescripcion();
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setPropiedades(String desc) {
		this.descripcion = desc;
	}

}
