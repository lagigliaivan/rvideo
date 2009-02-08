package servidor;

import java.net.DatagramSocket;

import javax.media.Format;
import javax.media.MediaLocator;

import org.apache.log4j.Logger;

public class ThrPlayer extends Thread {
	
	RtpTransmiter rtpTransmit = null;
	DatagramSocket dgSocket = null;
	String remoteAddress = null;
	int remoteMediaPort ;
	Format fmt = null;
	Logger logger = Logger.getLogger(this.getClass().getName());

	boolean conected = false;
	
	SipManager sipManager;
	int sipIndexConexion = 0;
	
	public ThrPlayer(DatagramSocket dgSocket,String remoteAddress,int remoteMediaPort,SipManager sipManager,int indexConexion){
	   
		setDgSocket(dgSocket);
		setRemoteAddress(remoteAddress);
		setRemoteMediaPort(remoteMediaPort);
		setSipManager(sipManager);
		setSipIndexConexion(indexConexion);
	}

	public void run(){
		
		int localPort = getDgSocket().getLocalPort();
		dgSocket.close();
	
		rtpTransmit = new RtpTransmiter(new MediaLocator("file://"+ System.getProperty("user.dir")+"/video/enviar.mpg"),
									 getRemoteAddress() , Integer.toString(localPort),getRemoteMediaPort() ,fmt);
		
		String result = rtpTransmit.start();

		// result will be non-null if there was an error. The return
		// value is a String describing the possible error. Print it.
		if (result != null) {
		    logger.error("Error : " + result);
		    return;
		}
		
		// Transmit for 60 seconds and then close the processor
		// This is a safeguard when using a capture data source
		// so that the capture device will be properly released
		// before quitting.
		// The right thing to do would be to have a GUI with a
		// "Stop" button that would call stop on RtpTransmiter
		try {
		    Thread.sleep(3000);
		} catch (InterruptedException ie) {
		}

		// Stop the transmission
		rtpTransmit.stop();
		
		String argv[] = new String[1];
//		argv[0]="192.168.1.12/"+localPort+"/55";
		//try {
			//argv[0]=InetAddress.getLocalHost().getHostAddress()+"/"+localPort+"/55";
			argv[0]=getSipManager().getStackAddress()+"/"+localPort+"/55";
		//} catch (UnknownHostException e) {
		//	e.printStackTrace();
		//}
				
		RtpReceiver avReceive = new RtpReceiver(argv,getSipManager(),getSipIndexConexion());
	
		
		if (!avReceive.initialize()) {
		    logger.error("Failed to initialize the sessions.");
		    System.exit(-1);
		}
			
		setConected(true);
		
	}
		
	public DatagramSocket getDgSocket() {
		return dgSocket;
	}
	
	
	public void setDgSocket(DatagramSocket dgSocket) {
		this.dgSocket = dgSocket;
	}

	public RtpTransmiter getRtpTransmit() {
		return rtpTransmit;
	}

	public void setRtpTransmit(RtpTransmiter rtpTransmit) {
		this.rtpTransmit = rtpTransmit;
	}

	
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getRemoteMediaPort() {
		return remoteMediaPort;
	}

	public void setRemoteMediaPort(int remoteMediaPort) {
		this.remoteMediaPort = remoteMediaPort;
	}


	public boolean isConected() {
		return conected;
	}

	public void setConected(boolean conected) {
		this.conected = conected;
	}

	public int getSipIndexConexion() {
		return sipIndexConexion;
	}

	public void setSipIndexConexion(int sipConexion) {
		this.sipIndexConexion = sipConexion;
	}

	public SipManager getSipManager() {
		return sipManager;
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}

	
	
	
}
