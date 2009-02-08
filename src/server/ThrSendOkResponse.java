package servidor;
import javax.sip.RequestEvent;

public class ThrSendOkResponse extends Thread{

	private RequestEvent event = null;
	private SipManager sipManager = null;
	
	public ThrSendOkResponse(RequestEvent event,SipManager sipManager){
		setEvent(event);
		setSipManager(sipManager);
	}
	
	public void run(){
		try {
			getSipManager().sendResponseToInvite(event);
		} catch (ExcGeneric e) {
			e.printStackTrace();
		}
	}

	public RequestEvent getEvent() {
		return event;
	}

	public void setEvent(RequestEvent event) {
		this.event = event;
	}

	public SipManager getSipManager() {
		return sipManager;
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}
}
