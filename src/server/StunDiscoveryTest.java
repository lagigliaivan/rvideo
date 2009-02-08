package server;
//package de.javawi.jstun.test;

import java.net.*;
import java.io.*;
import java.util.logging.*;

import de.javawi.jstun.attribute.*;
import de.javawi.jstun.header.*;
import de.javawi.jstun.util.*;

public class StunDiscoveryTest {
	private static Logger logger = Logger.getLogger("de.javawi.stun.test.DiscoveryTest");
	String stunServer;
	int port;
	int timeoutInitValue = 300; //ms
	MappedAddress ma = null;
	ChangedAddress ca = null;
	boolean nodeNatted = true;
	int localPortTest1 = 0;
	StunDiscoveryInfo di = null;
	
	
	public StunDiscoveryTest(String stunServer, int port) {
		super();
		this.stunServer = stunServer;
		this.port = port;
	}
	
	/*public static void main(String[] args) {
		try {
			Handler fh = new FileHandler("logging.txt");
			fh.setFormatter(new SimpleFormatter());
			Logger.getLogger("de.javawi.stun").addHandler(fh);
			Logger.getLogger("de.javawi.stun").setLevel(Level.ALL);
			StunDiscoveryTest test = new StunDiscoveryTest("stun.xten.net", 3478);
			// iphone-stun.freenet.de:3478
			// larry.gloo.net:3478
			// stun.xten.net:3478
			logger.error(test.test());
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
	}
	*/
	public StunDiscoveryInfo test() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException{
		ma = null;
		ca = null;
		nodeNatted = true;
		localPortTest1 = 0; 
		di = new StunDiscoveryInfo();
		if (test1()) {
			if (test2()) {
				test3();
			}
		}
		return di;
	}
	
	private boolean test1() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			try {
				// Test 1 including response
				DatagramSocket socket = new DatagramSocket();
				localPortTest1 = socket.getLocalPort();
				socket.connect(InetAddress.getByName(stunServer), port);
				socket.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				sendMH.addMessageAttribute(changeRequest);
				
				byte[] data = sendMH.getBytes();
				DatagramPacket send = new DatagramPacket(data, data.length, InetAddress.getByName(stunServer), port);
				//send.setAddress(InetAddress.getByName(stunServer));
				//send.setPort(port);
				socket.send(send);
				logger.finer("Test 1: Binding Request sent.");
			
				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					socket.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
				}
				socket.close();
				
				ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
				ca = (ChangedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					logger.config("Message header contains errorcode message attribute.");
					return false;
				}
				if ((ma == null) || (ca == null)) {
					di.setError(700, "The server is sending incomplete response (Mapped Address and Changed Address message attributes are missing). The client should not retry.");
					logger.config("Response does not contain a mapped address or changed address message attribute.");
					return false;
				} else {
					
				//	de.javawi.jstun.util.Address a = ma.getAddress();
									
					di.setPublicIP(ma.getAddress().getInetAddress());
					
					
					if ((ma.getPort() == socket.getLocalPort()) && (ma.getAddress().getInetAddress().equals(socket.getLocalAddress()))) {
						logger.fine("Node is not natted.");
						nodeNatted = false;
					} else {
						logger.fine("Node is natted.");
					}
					return true;
				}
			} catch (SocketTimeoutException ste) {
				if (timeSinceFirstTransmission < 7900) {
					logger.finer("Test 1: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					// node is not capable of udp communication
					logger.finer("Test 1: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
					di.setBlockedUDP();
					logger.fine("Node is not capable of udp communication.");
					return false;
				}
			} 
		}
	}
		
	private boolean test2() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			try {
				// Test 2 including response
				DatagramSocket sendSocket = new DatagramSocket();
				sendSocket.connect(InetAddress.getByName(stunServer), port);
				sendSocket.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				changeRequest.setChangeIP();
				changeRequest.setChangePort();
				sendMH.addMessageAttribute(changeRequest);
				
				//ResponseAddress ra = new ResponseAddress();
				//ra.setAddress(ma.getAddress());
				//ra.setPort(sendSocket.getLocalPort());
				//sendMH.addMessageAttribute(ra);
				 
				byte[] data = sendMH.getBytes(); 
				DatagramPacket send = new DatagramPacket(data, data.length, InetAddress.getByName(stunServer), port);
				//send.setAddress(InetAddress.getByName(stunServer));
				//send.setPort(port);
				sendSocket.send(send);
				logger.finer("Test 2: Binding Request sent.");
				
				int localPort = sendSocket.getLocalPort();
				InetAddress localAddress = sendSocket.getLocalAddress();
				
				sendSocket.close();
				
				DatagramSocket receiveSocket = new DatagramSocket(localPort, localAddress);
				receiveSocket.connect(ca.getAddress().getInetAddress(), ca.getPort());
				receiveSocket.setSoTimeout(timeout);
				
				MessageHeader receiveMH = new MessageHeader();
				while(!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					receiveSocket.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
				}
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					logger.config("Message header contains errorcode message attribute.");
					return false;
				}
				if (!nodeNatted) {
					di.setOpenAccess();
					logger.fine("Node has open access to the internet (or, at least the node is a full-cone NAT without translation).");
				} else {
					di.setFullCone();
					logger.fine("Node is behind a full-cone NAT.");
				}
				return false;
			} catch (SocketTimeoutException ste) {
				if (timeSinceFirstTransmission < 7900) {
					logger.finer("Test 2: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					logger.finer("Test 2: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
					if (!nodeNatted) {
						di.setSymmetricUDPFirewall();
						logger.fine("Node is behind a symmetric udp firewall.");
						return false;
					} else {
						// not is natted
						// redo test 1 with address and port as offered in the changed-address message attribute
						return test1Redo();
					}
				}
			}
		}
	}
	
	private boolean test1Redo() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageHeaderParsingException{
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			//	redo test 1 with address and port as offered in the changed-address message attribute
			try {
				//	Test 1 with changed port and address values
				DatagramSocket socket = new DatagramSocket(localPortTest1);
				socket.connect(ca.getAddress().getInetAddress(), ca.getPort());
				socket.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				sendMH.addMessageAttribute(changeRequest);
				
				byte[] data = sendMH.getBytes();
				DatagramPacket send = new DatagramPacket(data, data.length, ca.getAddress().getInetAddress(), ca.getPort());
				//send.setAddress(ca.getAddress().getInetAddress());
				//send.setPort(ca.getPort());
				socket.send(send);
				logger.finer("Test 1 redo with changed address: Binding Request sent.");
				
				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					socket.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
				}
				MappedAddress ma2 = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					logger.config("Message header contains errorcode message attribute.");
					return false;
				}
				if (ma2 == null) {
					di.setError(700, "The server is sending incomplete response (Mapped Address message attribute is missing). The client should not retry.");
					logger.config("Response does not contain a mapped address message attribute.");
					return false;
				} else {
					if ((ma.getPort() != ma2.getPort()) || (!(ma.getAddress().getInetAddress().equals(ma2.getAddress().getInetAddress())))) {
						di.setSymmetricCone();
						logger.fine("Node is behind a symmetric NAT.");
						return false;
					}
				}
				return true;
			} catch (SocketTimeoutException ste2) {
				if (timeSinceFirstTransmission < 7900) {
					logger.config("Test 1 redo with changed address: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					logger.config("Test 1 redo with changed address: Socket timeout while receiving the response.  Maximum retry limit exceed. Give up.");
					return false;
				}
			}
		}
	}
	
	private void test3() throws UtilityException, SocketException, UnknownHostException, IOException, MessageAttributeParsingException, MessageAttributeException, MessageHeaderParsingException {
		int timeSinceFirstTransmission = 0;
		int timeout = timeoutInitValue;
		while (true) {
			try {
				// Test 3 including response
				DatagramSocket sendSocket = new DatagramSocket();
				sendSocket.connect(InetAddress.getByName(stunServer), port);
				sendSocket.setSoTimeout(timeout);
				
				MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
				sendMH.generateTransactionID();
				
				ChangeRequest changeRequest = new ChangeRequest();
				changeRequest.setChangePort();
				sendMH.addMessageAttribute(changeRequest);
				
				//ResponseAddress ra = new ResponseAddress();
				//ra.setAddress(ma.getAddress());
				//ra.setPort(sendSocket.getLocalPort());
				//sendMH.addMessageAttribute(ra);
				
				byte[] data = sendMH.getBytes();
				DatagramPacket send = new DatagramPacket(data, data.length, InetAddress.getByName(stunServer), port);
				//send.setAddress(InetAddress.getByName(stunServer));
				//send.setPort(port);
				sendSocket.send(send);
				logger.finer("Test 3: Binding Request sent.");
				
				int localPort = sendSocket.getLocalPort();
				InetAddress localAddress = sendSocket.getLocalAddress();
				
				sendSocket.close();
				
				DatagramSocket receiveSocket = new DatagramSocket(localPort, localAddress);
				receiveSocket.connect(InetAddress.getByName(stunServer), ca.getPort());
				receiveSocket.setSoTimeout(timeout);
				
				MessageHeader receiveMH = new MessageHeader();
				while (!(receiveMH.equalTransactionID(sendMH))) {
					DatagramPacket receive = new DatagramPacket(new byte[200], 200);
					receiveSocket.receive(receive);
					receiveMH = MessageHeader.parseHeader(receive.getData());
				}
				ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);
				if (ec != null) {
					di.setError(ec.getResponseCode(), ec.getReason());
					logger.config("Message header contains errorcode message attribute.");
					return;
				}
				if (nodeNatted) {
					di.setRestrictedCone();
					logger.fine("Node is behind a restricted NAT.");
				}
			} catch (SocketTimeoutException ste) {
				if (timeSinceFirstTransmission < 7900) {
					logger.finer("Test 3: Socket timeout while receiving the response.");
					timeSinceFirstTransmission += timeout;
					int timeoutAddValue = (timeSinceFirstTransmission * 2);
					if (timeoutAddValue > 1600) timeoutAddValue = 1600;
					timeout = timeoutAddValue;
				} else {
					logger.finer("Test 3: Socket timeout while receiving the response. Maximum retry limit exceed. Give up.");
					di.setPortRestrictedCone();
					logger.fine("Node is behind a port restricted NAT.");
					return;
				}
			}
		}
	}
}