package consola;
/*
 * @(#)RtpReceiver.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Panel;
import java.net.InetAddress;
import java.util.Vector;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.control.BufferControl;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPControl;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.ByeEvent;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.event.StreamMappedEvent;
import javax.swing.JFrame;


/**
 * RtpReceiver to receive RTP transmission using the new RTP API.
 */

/**
 * EJEMPLO DE USO CON WEBCAM java AVReceive 192.168.1.12/5555/55
 * 192.168.1.12: IP DESDE DONDE VOY A RECIBIR DATO
 * 5554: PUERTO LOCAL DONDE VOY A RECIBIR EL DATO
 * 55: TTL 
 */

public class RtpReceiver extends Thread implements ReceiveStreamListener, SessionListener, 
ControllerListener
{
    String sessions[] = null;
    RTPManager mgrs[] = null;
    Vector <PlayerWindow> playerWindows = null;

    boolean dataReceived = false;
    Object dataSync = new Object();
    SipManager sipManager = null;
    int sipIndexConexion = 0;
    ThrKeepAliver thrKeepAliver = null;
    
    
    public int getSipIndexConexion() {
		return sipIndexConexion;
	}

	public void setSipIndexConexion(int sipIndexConexion) {
		this.sipIndexConexion = sipIndexConexion;
	}

	public SipManager getSipManager() {
		return sipManager;
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}

	public RtpReceiver(String sessions[],SipManager sipManager, int sipIndexConexion,ThreadGroup thrGrp, String name) {
		
		super(thrGrp, name);
		setSipManager(sipManager);
		setSipIndexConexion(sipIndexConexion);
		this.sessions = sessions;
		
		
    }
    
	public synchronized void run(){
		try {
			this.wait();
		} catch (InterruptedException e) {
			
		}
	}
    protected boolean initialize() {

    	try {
    		InetAddress ipAddr;
    		SessionAddress localAddr = new SessionAddress();
    		SessionAddress destAddr;
    		
    		mgrs = new RTPManager[sessions.length];
    		playerWindows = new Vector <PlayerWindow> ();
    		
    		SessionLabel session;
    		
    		// Open the RTP sessions.
    		for (int i = 0; i < sessions.length; i++) {
    			
    			// Parse the session addresses.
    			try {
    				session = new SessionLabel(sessions[i]);
    			} catch (IllegalArgumentException e) {
    				System.err.println("Failed to parse the session address given: " + sessions[i]);
    				return false;
    			}
    			
    			System.err.println("  - Open RTP session for: addr: " + session.addr + " port: " + session.port + " ttl: " + session.ttl);
    			
    			mgrs[i] = (RTPManager) RTPManager.newInstance();
    			mgrs[i].addSessionListener(this);
    			mgrs[i].addReceiveStreamListener(this);
    			
    			ipAddr = InetAddress.getByName(session.addr);
    			
    			if( ipAddr.isMulticastAddress()) {
    				// local and remote address pairs are identical:
    				localAddr= new SessionAddress( ipAddr,session.port,	session.ttl);
    				destAddr = new SessionAddress( ipAddr,	session.port,session.ttl);
    			} else {
    				localAddr= new SessionAddress( InetAddress.getByName(getSipManager().getStackAddress()),session.port);
    				destAddr = new SessionAddress( ipAddr, session.port);
    				
    			}
    			
    			mgrs[i].initialize( localAddr);
    			
    			// You can try out some other buffer size to see
    			// if you can get better smoothness.
    			BufferControl bc = (BufferControl)mgrs[i].getControl("javax.media.control.BufferControl");
    			if (bc != null)
    			//	bc.setBufferLength(350);
    				bc.setBufferLength(5000);
    			mgrs[i].addTarget(destAddr);
    		}
    		
    	} catch (Exception e){
    		System.err.println("Cannot create the RTP Session: " + e.getMessage());
    		return false;
    	}
    	
    	// Wait for data to arrive before moving on.
    	
    	long then = System.currentTimeMillis();
    	long waitingPeriod = 30000;  // wait for a maximum of 30 secs.
    	
    	try{
    		synchronized (dataSync) {
    			while (!dataReceived && 
    					System.currentTimeMillis() - then < waitingPeriod) {
    				if (!dataReceived)
    					System.err.println("  - Waiting for RTP data to arrive...");
    				dataSync.wait(1000);
    			}
    		}
    	} catch (Exception e) {}
    	
    	if (!dataReceived) {
    		System.err.println("No RTP data was received.");
    		close();
    		return false;
    	}
    	
    	return true;
    }


    public boolean isDone() {
	    return playerWindows.size() == 0;
    }


    /**
     * Close the players and the session managers.
     */
    public void close() {

	for (int i = 0; i < playerWindows.size(); i++) {
	    try {
		((PlayerWindow)playerWindows.elementAt(i)).close();
	    } catch (Exception e) {}
	}

	playerWindows.removeAllElements();

	// close the RTP session.
	for (int i = 0; i < mgrs.length; i++) {
	    if (mgrs[i] != null) {
                mgrs[i].removeTargets( "Closing session from RtpReceiver");
                mgrs[i].dispose();
		mgrs[i] = null;
	    }
	}
    }


    PlayerWindow find(Player p) {
	for (int i = 0; i < playerWindows.size(); i++) {
	    PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
	    if (pw.player == p)
		return pw;
	}
	return null;
    }

    PlayerWindow find(ReceiveStream strm) {
    
    	for (int i = 0; i < playerWindows.size(); i++) {
    		PlayerWindow pw = (PlayerWindow)playerWindows.elementAt(i);
    		if (pw.stream == strm)
    			return pw;
    	}
    	return null;

    }


    /**
     * SessionListener.
     */
    public synchronized void update(SessionEvent evt) {
	if (evt instanceof NewParticipantEvent) {
	    Participant p = ((NewParticipantEvent)evt).getParticipant();
	    System.err.println("  - A new participant had just joined: " + p.getCNAME());
	}
    }


    /**
     * ReceiveStreamListener
     */
    public synchronized void update( ReceiveStreamEvent evt) {


	Participant participant = evt.getParticipant();	// could be null.
	ReceiveStream stream = evt.getReceiveStream();  // could be null.

	if (evt instanceof RemotePayloadChangeEvent) {
     
	    System.err.println("  - Received an RTP PayloadChangeEvent.");
	    System.err.println("Sorry, cannot handle payload change.");
	    //System.exit(0);
	    return;

	}
    
	else if (evt instanceof NewReceiveStreamEvent) {

	    try {
		stream = ((NewReceiveStreamEvent)evt).getReceiveStream();
		DataSource ds = stream.getDataSource();

		// Find out the formats.
		RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
		if (ctl != null){
		    System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());
		} else
		    System.err.println("  - Recevied new RTP stream");

		if (participant == null)
		    System.err.println("      The sender of this stream had yet to be identified.");
		else {
		    System.err.println("      The stream comes from: " + participant.getCNAME()); 
		}

		// create a player by passing datasource to the Media Manager
		Player p = javax.media.Manager.createPlayer(ds);
		if (p == null)
		    return;

		p.addControllerListener(this);
		p.realize();
		PlayerWindow pw = new PlayerWindow(p, stream);
		playerWindows.addElement(pw);
		// Notify intialize() that a new stream had arrived.
		synchronized (dataSync) {
		    dataReceived = true;
		    dataSync.notifyAll();
		}

	    } catch (Exception e) {
		System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
		return;
	    }
        
	}

	else if (evt instanceof StreamMappedEvent) {

	     if (stream != null && stream.getDataSource() != null) {
		DataSource ds = stream.getDataSource();
		// Find out the formats.
		RTPControl ctl = (RTPControl)ds.getControl("javax.media.rtp.RTPControl");
		System.err.println("  - The previously unidentified stream ");
		if (ctl != null)
		    System.err.println("      " + ctl.getFormat());
		System.err.println("      had now been identified as sent by: " + participant.getCNAME());
	     }
	}

	else if (evt instanceof ByeEvent) {

	     System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
	     PlayerWindow pw = find(stream);
	     if (pw != null) {
		pw.close();
		playerWindows.removeElement(pw);
	     }
	}

    }


    /**
     * ControllerListener for the Players.
     */
    public synchronized void controllerUpdate(ControllerEvent ce) {

	Player p = (Player)ce.getSourceController();

	if (p == null)
	    return;

	// Get this when the internal players are realized.
	if (ce instanceof RealizeCompleteEvent) {
	    PlayerWindow pw = find(p);
	    if (pw == null) {
	    	// Some strange happened.
	    	System.err.println("Internal error!");
	    	return;
	    }
	    pw.initialize();
	    pw.setVisible(true);
	    p.start();
	    
	    SipConexion sipCon = null;
		try {
			sipCon = getSipManager().getAllocator().getSipConexion(getSipIndexConexion());
			
		} catch (ExcGeneric e) {
			System.err.println(e.getMessage());
			return;
		}
	    String toUser = sipCon.getToUser();
	    String toDomain = sipCon.getToDomain();
	    
	    thrKeepAliver = new ThrKeepAliver(getSipManager(),toUser,toDomain,this);
	    thrKeepAliver.start();
	}

	if (ce instanceof ControllerErrorEvent) {
	    p.removeControllerListener(this);
	    PlayerWindow pw = find(p);
	    if (pw != null) {
		pw.close();	
		playerWindows.removeElement(pw);
	    }
	    System.err.println("RtpReceiver internal error: " + ce);
	}

    }


    /**
     * A utility class to parse the session addresses.
     */
    class SessionLabel {

	public String addr = null;
	public int port;
	public int ttl = 1;

	SessionLabel(String session) {

	    int off;
	    String portStr = null, ttlStr = null;

	    if (session != null && session.length() > 0) {
		while (session.length() > 1 && session.charAt(0) == '/')
		    session = session.substring(1);

		// Now see if there's a addr specified.
		off = session.indexOf('/');
		if (off == -1) {
		    if (!session.equals(""))
			addr = session;
		} else {
		    addr = session.substring(0, off);
		    session = session.substring(off + 1);
		    // Now see if there's a port specified
		    off = session.indexOf('/');
		    if (off == -1) {
			if (!session.equals(""))
			    portStr = session;
		    } else {
			portStr = session.substring(0, off);
			session = session.substring(off + 1);
			// Now see if there's a ttl specified
			off = session.indexOf('/');
			if (off == -1) {
			    if (!session.equals(""))
				ttlStr = session;
			} else {
			    ttlStr = session.substring(0, off);
			}
		    }
		}
	    }

	    if (addr == null)
		throw new IllegalArgumentException();

	    if (portStr != null) {
		try {
		    Integer integer = Integer.valueOf(portStr);
		    if (integer != null)
			port = integer.intValue();
		} catch (Throwable t) {
		    throw new IllegalArgumentException();
		}
	    } else
		throw new IllegalArgumentException();

	    if (ttlStr != null) {
		try {
		    Integer integer = Integer.valueOf(ttlStr);
		    if (integer != null)
			ttl = integer.intValue();
		} catch (Throwable t) {
		    throw new IllegalArgumentException();
		}
	    }
	}
    }


    /**
     * GUI classes for the Player.
     */
    class PlayerWindow extends JFrame {
    
	private static final long serialVersionUID = 1L;
	Player player;
	ReceiveStream stream;
	
	
	PlayerWindow(Player p, ReceiveStream strm) {
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		player = p;
	    stream = strm;
	    
	}
	
	public void initialize() {
	    add(new PlayerPanel(player));
	   // PlayerPanel p ;
	    
	}

	public void close() {
	    player.close();
	    setVisible(false);
	    dispose();
	}

	public void addNotify() {
	    super.addNotify();
	    pack();
	}
	@SuppressWarnings("deprecation")
	protected void processEvent(AWTEvent e){
		if(e.getID()==201){
			System.err.println("sendig bye...");
			try {
				this.setVisible(false);
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
			try {
				getSipManager().sendBy(getSipIndexConexion());
				getThrKeepAliver().stop();
				interrupt();
				stop();
			} catch (ExcGeneric e1) {
				e1.printStackTrace();
			}
			
		}
	}
}


    /**
     * GUI classes for the Player.
     */
    class PlayerPanel extends Panel {

	private static final long serialVersionUID = 1L;
	Component vc, cc;
	
	PlayerPanel(Player p) {
	    setLayout(new BorderLayout());
	    if ((vc = p.getVisualComponent()) != null)
		add("Center", vc);
	    if ((cc = p.getControlPanelComponent()) != null)
		add("South", cc);
	}

	public Dimension getPreferredSize() {
	    int w = 0, h = 0;
	    if (vc != null) {
		Dimension size = vc.getPreferredSize();
		w = size.width;
		h = size.height;
	    }
	    if (cc != null) {
		Dimension size = cc.getPreferredSize();
		if (w == 0)
		    w = size.width;
		h += size.height;
	    }
	    if (w < 160)
		w = 160;
	    return new Dimension(w, h);
	}
    }


	public ThrKeepAliver getThrKeepAliver() {
		return thrKeepAliver;
	}

	public void setThrKeepAliver(ThrKeepAliver thrKeepAliver) {
		this.thrKeepAliver = thrKeepAliver;
	}



}
