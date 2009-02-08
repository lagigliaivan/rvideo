package consola;
import java.util.Vector;

public class MediaDescriptor {
	
	private int rtpPort = 8000;
	private String userSession = "user";
	private String media = "video";
	private String dirIp = "";
	private String userAgent = "user";
	private String info = "sistema monitoreo";
	private Vector <String>desc = new Vector<String>(1,1);
	
	public MediaDescriptor(){
		
		
	}
	
	
	public String getDirIp() {
		return dirIp;
	}
	public void setDirIp(String dirIp) {
		this.dirIp = dirIp;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public int getRtpPort() {
		return rtpPort;
	}
	public void setRtpPort(int rtpPort) {
		this.rtpPort = rtpPort;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getUserSession() {
		return userSession;
	}
	public void setUserSession(String userSession) {
		this.userSession = userSession;
	}
	public Vector getMediaDescritpion(){
	 	
		desc.addElement(new String ("i="+ getInfo() +" \r\n"));
		desc.addElement(new String ("s="+ getUserAgent() +"\r\n"));	
		desc.addElement(new String ("c=IN IP4 "+ getDirIp()+ "\r\n"));
	 	desc.addElement(new String ("m=" + getMedia() +" " + getRtpPort() + " RTP/AVP 97 98\r\n"));
		desc.addElement(new String ("a=rtpmap:97 iLBC/8000\r\n"));
		desc.addElement(new String ("a=rtpmap:98 iLBC/8000\r\n"));
		
    	
    	return desc; 
    }


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}

}
