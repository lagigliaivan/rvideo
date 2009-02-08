package server;

import javax.sip.message.Response;

import org.apache.log4j.Logger;



public class ThrKeepAliver extends Thread{

	private SipManager sipManager;
	private String userTo;
	private String domainTo; 
	private Response response = null;
	private int sipConexionIndex = 0;
	private RtpReceiver rtpReciver = null;
	private RtpTransmiter rtpTransmiter = null;
	private Logger logger  = Logger.getLogger(this.getClass().getName());
	int timeOut = 0;
	int reintento = 0;

	public ThrKeepAliver(SipManager manager,String userTo, String domainTo,RtpReceiver rtpReciver){
		setSipManager(manager);
		setUserTo(userTo);
		setDomainTo(domainTo);
		setRtpReciver(rtpReciver);

		try {
			sipConexionIndex = getSipManager().createMessageSipConexion(userTo, domainTo);
		} catch (ExcGeneric e) {
			logger.error("No se pudo generar el hilo");
			return;
		}

	}

	public ThrKeepAliver(SipManager manager,String userTo, String domainTo,RtpTransmiter rtpTransmiter){
		setSipManager(manager);
		setUserTo(userTo);
		setDomainTo(domainTo);
		setRtpTransmiter(rtpTransmiter);

		try {
			sipConexionIndex = getSipManager().createMessageSipConexion(userTo, domainTo);
		} catch (ExcGeneric e) {
			logger.error("No se pudo generar el hilo");
			return;
		}

	}



	public void run() {
		boolean primeraVez = true;

		while(true){
			if(primeraVez){
				try {Thread.sleep(15000);} catch (InterruptedException e) {}
			}
			else{
				try {Thread.sleep(7000);} catch (InterruptedException e) {}
			}

			try {
				getSipManager().sendMessageRequest(getSipConexionIndex());
			} catch (ExcGeneric e2) {
				logger.error("Se perdio la conexion con la webcam");
				if(getRtpReciver()!= null){
					getRtpReciver().close();
				}
				else{
					getRtpTransmiter().stop();
					getSipManager().setTransmiting(false);
				}
				return;
			}			
		}	
	}


	public SipManager getSipManager() {
		return sipManager;
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}

	public String getDomainTo() {
		return domainTo;
	}

	public String getUserTo() {
		return userTo;
	}

	public void setDomainTo(String domainTo) {
		this.domainTo = domainTo;
	}

	public void setUserTo(String userTo) {
		this.userTo = userTo;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public int getSipConexionIndex() {
		return sipConexionIndex;
	}

	public void setSipConexionIndex(int sipConexionIndex) {
		this.sipConexionIndex = sipConexionIndex;
	}

	public RtpReceiver getRtpReciver() {
		return rtpReciver;
	}

	public void setRtpReciver(RtpReceiver rtpReciver) {
		this.rtpReciver = rtpReciver;
	}

	public RtpTransmiter getRtpTransmiter() {
		return rtpTransmiter;
	}

	public void setRtpTransmiter(RtpTransmiter rtpTransmiter) {
		this.rtpTransmiter = rtpTransmiter;
	}
	
	
}
