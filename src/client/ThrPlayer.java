package client;
import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.media.Format;
import javax.media.MediaLocator;

public class ThrPlayer extends Thread {
	
	RtpTransmiter rtpTransmit = null;
	DatagramSocket dgSocket = null;
	String remoteAddress = null;
	int remoteMediaPort ;
	Format fmt = null;
	String nameThrReceiver;
	ThreadGroup thrGrp;

	boolean conected = false;
	
	SipManager sipManager;
	int sipIndexConexion = 0;
	
	public ThrPlayer(DatagramSocket dgSocket,String remoteAddress,
					 int remoteMediaPort,SipManager sipManager,
					 int indexConexion,ThreadGroup threadGrp, String name)
	{
	    
		setThrGrp(threadGrp);
		setNameThrReceiver(name);
		setDgSocket(dgSocket);
		setRemoteAddress(remoteAddress);
		setRemoteMediaPort(remoteMediaPort);
		setSipManager(sipManager);
		setSipIndexConexion(indexConexion);
	}

	public void run(){
		String video = "file://" + System.getProperty("user.dir") + File.separator + "video" + File.separator + "enviar.mpg";
		int localPort = getDgSocket().getLocalPort();
		dgSocket.close();
		rtpTransmit = new RtpTransmiter(new MediaLocator(video), getRemoteAddress() , 
										Integer.toString(localPort),getRemoteMediaPort() ,
										fmt);
		
		try {
			rtpTransmit.setLocalAddres(InetAddress.getByName(getSipManager().getStackAddress()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String result = rtpTransmit.start();

		// result will be non-null if there was an error. The return
		// value is a String describing the possible error. Print it.
		if (result != null) {
		    System.err.println("Error : " + result);
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
		argv[0] = getSipManager().getStackAddress().toString() + "/" + localPort + "/55";
						
		RtpReceiver avReceive = new RtpReceiver(argv,getSipManager(),getSipIndexConexion(),getThrGrp(),getNameThrReceiver());
	
		avReceive.start();
		
		if (!avReceive.initialize()) {
		    System.err.println("Failed to initialize the sessions.");
		    //System.exit(-1);
		    return;
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

	public String getNameThrReceiver() {
		return nameThrReceiver;
	}

	public ThreadGroup getThrGrp() {
		return thrGrp;
	}

	public void setNameThrReceiver(String nameThrReceiver) {
		this.nameThrReceiver = nameThrReceiver;
	}

	public void setThrGrp(ThreadGroup thrGrp) {
		this.thrGrp = thrGrp;
	}

	
	
	
}
