package client;


import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.ProxyAuthenticate;
import gov.nist.javax.sip.header.ProxyAuthorization;
import gov.nist.javax.sip.header.WWWAuthenticate;
import gov.nist.javax.sip.stack.SIPClientTransaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

import javax.sdp.SdpException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;


public class SipConexion implements Cloneable{

	//Sip URI que aparecera en la primerla linea del request junto al metodo
	private SipURI uriFirstLine = null;
	
	//Sip URI que aparecera en el campo TO del header
	private SipURI URIto;
	
	//Sip URI que aparecera en el campo From del header
	private SipURI URIfrom;
	
	//toUser: A que usuario quiero conectarme 
	private String toUser;
	
	//fromUser: Desde el usuario que me conecto 
	private String fromUser;
	
	//Dominio del usuario al que quiero conectarme (usuario@dominio)
	private String toDomain;
	
	//Dominio del usuario que inicia la conexion (usuario@dominio)
	private String fromDomain;
	
	//nombre de usuario para loguearse en el registrar sip
	private String userName = null;
	
	//password del usuario
	private String userPass = null;
	
	//objeto que inicializo el api para SIP
	private SipManager sipManager;
	
	//campo CSeq del header, lo inicializo a 1
	private int cSeq = 1;
	
	//direccion ip de la pc remota a la que me conecto
	private String remoteIpContact = null;
	
	//puerto remoto al que me conecto
	private int remotePortContact = 0;
	
	// Call-ID header. Representa una relacion entre dos o mas					------INVITE------>
	// usuarios. Representa una invitacion y a todos los mensajes				<-------OK---------
	// subsecuentes.  El server asocia todos los mensajes pertencientes			--------ACK-------->
	// a una sesion mediante este header.   
	private CallIdHeader callIdHeader;
	
	// CSeq header: Contiene un numero entero y el metodo del mensaje
	// La parte entera se utiliza para identifica distintos mensajes
	// dentro de la misma sesion
	private CSeqHeader cSeqHeader;

	//Sip URI que aparece en el header contact
	private SipURI contactURI = null;
	
	private Address contactAddress = null;
	
	//Este header provee una URL mediante la cual el UA puede ser
	//alcanzado directamente
	private Contact contactHeader = null;
	
	//Campo from: Indica quien es el que inicia el request
	private FromHeader fromHeader;
	
	//Campo to: indica a quien se le envia el request
	private ToHeader toHeader;
	
	//Header Forward
	private MaxForwardsHeader maxForwardsHeader;
	
	private ViaHeader viaHeader;
	
	private static int MAX_FORWARDS = 70;
	
	//Direcciones que aparecen en el campo TO y FROM 
	// ej. TO : Nombre Apellido <sip: nombre@dominio>
	//Nombre Apellido = toAddress
	private Address toAddress;
	
	private Address fromAddress;
	
	private byte[] callIdBytes;
	
	private String callId;

	private byte[] tagBytes;
	
	private String tag;

	private Random randomCallId;
	
	private Random randomTag;
	
	//Nombres que apareceran en las direcciones TO y FROM 
	 
	private String toRealName = "";
	
	private String fromRealName = "";
		
	private SIPClientTransaction clientTransaction;
	
	//El metodo por defecto del request sera REGISTER
	private String sipMethod = "REGISTER";
	
	//direccion ip donde est� corriendo la aplicacion
	private String stackAddress;
	
	//datos cabecera request REGISTER , response Unauthorized y response OK  
	private String realm;
		
	private String ipRemote = null;

	private int mediaPortRemote = 0;
		
	private Timestamp timeStamp = null;
	
	private ArrayList <ViaHeader> viaHeaders = new ArrayList <ViaHeader>();
	/**
	 * Constructor
	 * @param toUser: Nombre de Usuario a quien le envio el mensaje
	 * @param fromUser: Nombre de Usuario desde donde envio el mensaje
	 * @param toDomain: Dominio del usuario a quien le envio el mensaje
	 * @param fromDomain: Dominio del usuario de donde envio el mensaje
	 */						   
	
	public SipConexion(String toUser, String fromUser, 
					   String toDomain,String fromDomain, 
					   SipManager sipManager)
	throws ExcGeneric{
	
		//toUser: A que usuario quiero conectarme 
		setToUser(toUser);
		//fromUser: Usuario que realiza la conexion
		setFromUser(fromUser);
		//Dominio del usuario al que quiero conectarme (usuario@dominio) 
		setToDomain(toDomain);
		//Dominio del usuario que inicia la conexion (usuario@dominio)
		setFromDomain(fromDomain);
		//objeto que inicializo el api para SIP
		setSipManager(sipManager);
		//objeto random que calcula el header CallId
		setRandomCallId(new Random());
		
		//objeto random que calcula el campo tag
		setRandomTag(new Random());
				
		//parece que est� de mas
		setUserName(fromUser);
								
		//inicializo los campos callId y tag
		setCallIdBytes(new byte[16]);
		
		randomCallId.nextBytes(getCallIdBytes());
		
		//armo el Call-ID de la forma  15216336265212avd@192.168.1.1
		setCallId(Tools.toHexString(getCallIdBytes()) + "@" + getSipManager().getStackAddress());
		
		setTagBytes(new byte[8]);
		
		randomTag.nextBytes(getTagBytes());
		
		setTag(Tools.toHexString(tagBytes));
		
		
		try{
					
			//campo Call-ID del header 
			setCallIdHeader(getSipManager().getHeaderFactory().createCallIdHeader(getCallId()));
					
			//campo CSeq del header 
			setCSeqHeader(getSipManager().getHeaderFactory().createCSeqHeader(getCSeq(), getSipMethod()));
						
			setMaxForwardsHeader(sipManager.getHeaderFactory().createMaxForwardsHeader(MAX_FORWARDS));
						
		}
		catch(ParseException e){
			System.out.println("error:" + e.getCause() + e.getMessage());
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
		catch(InvalidArgumentException e){
			System.out.println("error:" + e.getCause() + e.getMessage());
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
			
		//armo el URI que va a ir en la primer linea del header sip
		//en el caso del REGISTER la primer linea lleva a parte del metodo, la direccion del registrar server
		try {
			setUriFirstLine(getSipManager().getAddressFactory().createSipURI(getToUser(),getFromDomain()));
		} catch (ParseException e) {
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
		
		try {
			//armo el campo Via del header. Es de la forma: SIP/2.0/UDP IPPUBLIC:5060;rport;branch
			
			viaHeader =	getSipManager().getHeaderFactory().createViaHeader(getSipManager().getStackAddress(),
																		   getSipManager().getLocalPort(),
																		   getSipManager().getTransport(),
																		   null);
			//rport, con esto indicamos al proxy que devuelva el paquete al puerto
			//desde donde lo recibio
			viaHeader.setParameter("rport", null);
			viaHeaders.add(viaHeader);
			
			//fromHeader y toHeader son iguales, ya que en el request register
			//el to y el from son identicos
			
			URIfrom = getSipManager().getAddressFactory().createSipURI(getFromUser(),getFromDomain());
			URIto = getSipManager().getAddressFactory().createSipURI(getToUser(),getToDomain());
			
			contactURI = getSipManager().getAddressFactory().createSipURI(getFromUser(),getSipManager().getStackAddress());
			contactURI.setPort(getSipManager().getLocalPort());
					
			fromAddress = getSipManager().getAddressFactory().createAddress(getFromUser(),URIfrom);
			toAddress = getSipManager().getAddressFactory().createAddress(getToUser(),URIto);
			contactAddress = getSipManager().getAddressFactory().createAddress(contactURI);
			
			fromHeader = getSipManager().getHeaderFactory().createFromHeader(fromAddress, getTag() + "");
			toHeader = getSipManager().getHeaderFactory().createToHeader(toAddress,null);
			
			contactHeader = new Contact();
			contactHeader.setHeaderName("Contact");
			
			contactHeader.setAddress(contactAddress);
			
		} catch (ParseException e) {
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		} catch (InvalidArgumentException e) {
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
		setTimeStamp(new Timestamp(System.currentTimeMillis()));
	}
		
	
		
	public void sendRequest(String metodo,SessionDescription descSdp)
	throws ExcGeneric{
		
		Request request;
		
		System.err.println("Request --> " + metodo);
		
		if(metodo != null){
			setSipMethod(metodo);
		}
		
		try{
			
			if(descSdp != null){

				request = sipManager.getMessageFactory().createRequest((javax.sip.address.URI)uriFirstLine,
						this.sipMethod,
						callIdHeader,
						cSeqHeader,
						fromHeader,
						toHeader,
						viaHeaders,
						maxForwardsHeader,getSipManager().getHeaderFactory().createContentTypeHeader("application","sdp"),descSdp);



			}else{

				request = getSipManager().getMessageFactory().createRequest((javax.sip.address.URI)uriFirstLine,
						this.sipMethod,
						callIdHeader,
						cSeqHeader,
						fromHeader,
						toHeader,
						viaHeaders,
						maxForwardsHeader);
			}
			
			request.addHeader(getContactHeader());
			clientTransaction = (SIPClientTransaction) getSipManager().getSipProvider().getNewClientTransaction(request);
			clientTransaction.sendRequest();
			
		}
		catch (ParseException e){
			System.out.println("error: messageFactory.createRequest" + e.getMessage() + e.getCause());
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
			
		}
		catch (TransactionUnavailableException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
		catch (SipException e){
			System.out.println("error: clientTransaction.sendRequest" + e.getMessage() + e.getCause());
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
	
	}
	
	public void sendRegisterRequest() throws ExcGeneric{
			
		//Request registerRequest = null;
		//armo el URI que va a ir en la primer linea del header sip
		//en el caso del REGISTER la primer linea lleva a parte del metodo, la direccion del registrar server
		
		try {
			setUriFirstLine(getSipManager().getAddressFactory().createSipURI(null,getFromDomain()));
		} catch (ParseException e) {
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
		
		sendRequest("REGISTER", null);
	
	}
	/**
	 * Envia un register autenticandoce en el registrar sip
	 * @param response: es el response obtenido al enviar un register sin autenticacion
	 */
	public void sendRegisterWithAutentic(Response response){
			
	
		WWWAuthenticate wwwAuthentic = (WWWAuthenticate) response.getHeader("WWW-Authenticate");
		setRealm(wwwAuthentic.getRealm());
	
		try{
			
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			String A1 =  getFromUser() + ":" + getRealm() + ":" + getUserPass();
			String A2 = "REGISTER" + ":" + "sip:" + getRealm();
			
			byte mdbytes[] = messageDigest.digest(A1.getBytes());
			
			String HA1 = Tools.toHexString(mdbytes);
									    
		    mdbytes = messageDigest.digest(A2.getBytes());
		    String HA2 = Tools.toHexString(mdbytes);
		   
		    String KD = HA1 + ":" + wwwAuthentic.getNonce();
		    
		    KD += ":" + HA2;
		    
		    mdbytes = messageDigest.digest(KD.getBytes());
		    String respons = Tools.toHexString(mdbytes);
		  					
			Authorization authorizationHeader= new Authorization();
			
			authorizationHeader.setRealm(getRealm());
			authorizationHeader.setResponse(respons);
			authorizationHeader.setUsername(getFromUser());
			authorizationHeader.setNonce(wwwAuthentic.getNonce());
				
			Address address = getSipManager().getAddressFactory().createAddress(contactURI);
			contactHeader = new Contact();
			contactHeader.setHeaderName("Contact");
			contactHeader.setAddress(address);
			authorizationHeader.setParameter("uri","sip:" + this.realm);
			
			Request registerRequestAuthen = getSipManager().getMessageFactory().createRequest(
					   uriFirstLine,
					   "REGISTER",
					   getCallIdHeader(),
					   getCSeqHeader(),
					   fromHeader,
					   toHeader,
					   viaHeaders,
					   getMaxForwardsHeader());


			registerRequestAuthen.addHeader(authorizationHeader);

			registerRequestAuthen.addHeader(contactHeader);
			clientTransaction = (SIPClientTransaction) getSipManager().getSipProvider().getNewClientTransaction(registerRequestAuthen);
			clientTransaction.sendRequest();
		
		}
		
		catch (ParseException e){
			System.out.println("error: messageFactory.createRequest" + e.getMessage() + e.getCause());
			
		}
		catch (TransactionUnavailableException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
		}
		catch (NoSuchAlgorithmException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
		}
		catch (SipException e){
			System.out.println("error: clientTransaction.sendRequest" + e.getMessage() + e.getCause());
		}
		
		
	}
	
	public void sendInviteRequest(SessionDescription descSdp) throws ExcGeneric
	{
		sendRequest("INVITE", descSdp);
	}
	
	public void sendInviteWithAuthentic(Response response,SessionDescription descSdp ){

			
		Request inviteRequest = null; 
		
		//armo el URI que va a ir en la primer linea del header sip
		//en el caso del INVITE la primer linea lleva a parte del metodo, nombre de usuario y dominio
			
		ProxyAuthenticate proxyAuthentic = (ProxyAuthenticate) response.getHeader("Proxy-Authenticate");
		setRealm(proxyAuthentic.getRealm());
			
		try{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			String A1 =  this.userName + ":" + this.realm + ":" + this.userPass;
			String A2 = this.sipMethod + ":" + "sip:" + this.realm;
			
			
			byte mdbytes[] = messageDigest.digest(A1.getBytes());
			
			String HA1 = Tools.toHexString(mdbytes);
		  									    
		    mdbytes = messageDigest.digest(A2.getBytes());
		    String HA2 = Tools.toHexString(mdbytes);
		  
		   
		    String KD = HA1 + ":" + proxyAuthentic.getNonce();
		    
		    KD += ":" + HA2;
		    
		    mdbytes = messageDigest.digest(KD.getBytes());
		    String respons = Tools.toHexString(mdbytes);
		        
		  
					
		    ProxyAuthorization proxyAuthorizationHeader= new ProxyAuthorization();

			
			proxyAuthorizationHeader.setRealm(getRealm());
			proxyAuthorizationHeader.setResponse(respons);
			proxyAuthorizationHeader.setUsername(getFromUser());

			proxyAuthorizationHeader.setNonce(proxyAuthentic.getNonce());
				
			proxyAuthorizationHeader.setParameter("uri","sip:" + this.realm);
			
			
			inviteRequest = getSipManager().getMessageFactory().createRequest(
						   uriFirstLine,
						   "INVITE",
						   getCallIdHeader(),
						   getCSeqHeader(),
						   fromHeader,
						   toHeader,
						   viaHeaders,
						   getMaxForwardsHeader(),
						   getSipManager().getHeaderFactory().createContentTypeHeader("application","sdp"),descSdp);


				inviteRequest.addHeader(proxyAuthorizationHeader);

				inviteRequest.addHeader(contactHeader);
				clientTransaction = (SIPClientTransaction) getSipManager().getSipProvider().getNewClientTransaction(inviteRequest);
				clientTransaction.sendRequest();
							
		}
		
		catch (ParseException e){
			System.out.println("error: messageFactory.createRequest" + e.getMessage() + e.getCause());
		}
		catch (TransactionUnavailableException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
		}
		catch (NoSuchAlgorithmException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
		}
		catch (SipException e){
			System.out.println("error: clientTransaction.sendRequest" + e.getMessage() + e.getCause());
		}
		
		
	
	}
	
	public String calcCallId(){
		
		String call;
		randomCallId.nextBytes(callIdBytes);
		call = Tools.toHexString(callIdBytes);
		call = call + "@" + getSipManager().getStackAddress();
		return call;
	
	}
		
	public boolean setToUser(String toUser,String toDomain) throws ExcGeneric{
		
		this.toUser = toUser;
		this.toDomain = toDomain;
		
//		Sip URI del agente que origina el request
		try{
			URIfrom = getSipManager().getAddressFactory().createSipURI(this.fromUser,this.fromDomain);
		}
		catch(ParseException e){
			throw new ExcGeneric("error:" + e.getCause() + e.getMessage());
		}
		
	return true;
	}
		
	public boolean setCSeq(int valor){
		
		if(valor < 0){
			System.out.println("error: No se puede setear el campo CSeq con un valor menor a 1");
			return false;
		}
		else{
			this.cSeq = valor;
			return true;
		}
	}
	
	public void setCSeqHeader(int numSeq,String method){
		
		
		try{
			this.cSeqHeader = getSipManager().getHeaderFactory().createCSeqHeader(numSeq,method);
		}
		catch(ParseException e){
			System.out.println("error:"+ e.getMessage() + e.getCause());
			return;
		}
		catch(InvalidArgumentException e){
			System.out.println("error:"+ e.getMessage() + e.getCause());
			return;
		}
		this.sipMethod = method;
		this.cSeq = numSeq;
	}
	
	public boolean setToRealName(String realName){
		if (realName == null){
			return false;
		}else{
			this.toRealName = realName;
			return true;
		}
		
	}
	
	public boolean setFromRealName(String realName){
		if (realName == null){
			return false;
		}else{
			this.fromRealName = realName;
			return true;
		}
	}
		
	public boolean setSipMethod(String method){
		setCSeqHeader(this.getCSeq(),method);
		this.sipMethod = method;
		return true;
		
	}
	
	public int getCSeq(){
		return this.cSeq;
	}
	
	public void incrementCSeq(){
		this.cSeq++;
		this.setCSeqHeader(this.cSeq,this.geMethod());
		
	}
	
	public boolean setRealm(String realm){
		
		this.realm = realm;
		return true;	
	}

	public boolean setUserName(String userName){
		
		this.userName = userName;
		return true;
	}
	
	public boolean setUserPass(String pass){
		
		this.userPass = pass;
		return true;
	}
	
	public String geMethod(){
		return this.sipMethod;
	} 
	public void setCallId(String callId){
	
		this.callId = callId;
		try{
			this.callIdHeader = getSipManager().getHeaderFactory().createCallIdHeader(callId);
		}
		catch(ParseException e){
			System.out.println("error:" + e.getMessage() + e.getCause());
			return;
		}
	} 
	
	public Object clone()
	{
	    Object clone = null;
	    try
	    {
	        clone = super.clone();
	    } 
	    catch(CloneNotSupportedException e)
	    {
	        // No deberia suceder
	    }
	    return clone;
	}

	public SessionDescription getSessionDescription(){
	    
		SessionDescription sdpDesc = null;
		
		try{
			sdpDesc = getSipManager().getSdpFactory().createSessionDescription(); 
		}
		catch(SdpException sdpException){
			sdpException.printStackTrace();
		}
		return sdpDesc;
	}
	
	public void sendBy(){
				
		try {
			sendRequest("BYE", null);
		} catch (ExcGeneric e) {
			e.printStackTrace();
		}
	  
	}
	
	public void sendByWhitAuthentic(Response response){
		
		Request byeRequest = null; 
		
		ProxyAuthenticate proxyAuthentic = (ProxyAuthenticate) response.getHeader("Proxy-Authenticate");
		setRealm(proxyAuthentic.getRealm());
			
		try{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			String A1 =  this.userName + ":" + this.realm + ":" + this.userPass;
			//String A2 = this.sipMethod + ":" + "sip:" + this.realm;
			String A2 = "BYE" + ":" + "sip:" + this.realm;
			
			byte mdbytes[] = messageDigest.digest(A1.getBytes());
			
			String HA1 = Tools.toHexString(mdbytes);
		  									    
		    mdbytes = messageDigest.digest(A2.getBytes());
		    String HA2 = Tools.toHexString(mdbytes);
		  
		   
		    String KD = HA1 + ":" + proxyAuthentic.getNonce();
		    
		    KD += ":" + HA2;
		    
		    mdbytes = messageDigest.digest(KD.getBytes());
		    String respons = Tools.toHexString(mdbytes);
		        
		  
					
		    ProxyAuthorization proxyAuthorizationHeader= new ProxyAuthorization();

			
			proxyAuthorizationHeader.setRealm(getRealm());
			proxyAuthorizationHeader.setResponse(respons);
			proxyAuthorizationHeader.setUsername(getFromUser());

			proxyAuthorizationHeader.setNonce(proxyAuthentic.getNonce());
				
			proxyAuthorizationHeader.setParameter("uri","sip:" + this.realm);
			
			getCSeqHeader().setMethod("BYE");
			
			try {
				getCSeqHeader().setSequenceNumber(getCSeqHeader().getSequenceNumber() + 1);
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			}
			
			
			byeRequest = getSipManager().getMessageFactory().createRequest(
						   uriFirstLine,
						   "BYE",
						   getCallIdHeader(),
						   getCSeqHeader(),
						   fromHeader,
						   toHeader,
						   viaHeaders,
						   getMaxForwardsHeader());


				byeRequest.addHeader(proxyAuthorizationHeader);
				clientTransaction = (SIPClientTransaction) getSipManager().getSipProvider().getNewClientTransaction(byeRequest);
				clientTransaction.sendRequest();
		
		}
		
		catch (ParseException e){
			System.out.println("error: messageFactory.createRequest" + e.getMessage() + e.getCause());
		}catch (TransactionUnavailableException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
		}catch (NoSuchAlgorithmException e){
			System.out.println("error: sipProvider.getNewClientTransaction" + e.getMessage() + e.getCause());
		}catch (SipException e){
			System.out.println("error: clientTransaction.sendRequest" + e.getMessage() + e.getCause());
		}
		
	}
	
	public String generateTag(){
		getRandomTag().nextBytes(getTagBytes());
		return Tools.toHexString(getTagBytes());
	}
	
	public CallIdHeader getCallIdHeader(){
		return this.callIdHeader;
	}

	public static int getMAX_FORWARDS() {
		return MAX_FORWARDS;
	}

	public static void setMAX_FORWARDS(int max_forwards) {
		MAX_FORWARDS = max_forwards;
	}

	public byte[] getCallIdBytes() {
		return callIdBytes;
	}

	public void setCallIdBytes(byte[] callIdBytes) {
		this.callIdBytes = callIdBytes;
	}

	public SIPClientTransaction getClientTransaction() {
		return clientTransaction;
	}

	public void setClientTransaction(SIPClientTransaction clientTransaction) {
		this.clientTransaction = clientTransaction;
	}

	public CSeqHeader getCSeqHeader() {
		return cSeqHeader;
	}

	public void setCSeqHeader(CSeqHeader seqHeader) {
		cSeqHeader = seqHeader;
	}

	public Address getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(Address fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromDomain() {
		return fromDomain;
	}

	public void setFromDomain(String fromDomain) {
		this.fromDomain = fromDomain;
	}

	public FromHeader getFromHeader() {
		return fromHeader;
	}

	public void setFromHeader(FromHeader fromHeader) {
		this.fromHeader = fromHeader;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public MaxForwardsHeader getMaxForwardsHeader() {
		return maxForwardsHeader;
	}

	public void setMaxForwardsHeader(MaxForwardsHeader maxForwardsHeader) {
		this.maxForwardsHeader = maxForwardsHeader;
	}

	public Random getRandomCallId() {
		return randomCallId;
	}

	public void setRandomCallId(Random randomCallId) {
		this.randomCallId = randomCallId;
	}

	public Random getRandomTag() {
		return randomTag;
	}

	public void setRandomTag(Random randomTag) {
		this.randomTag = randomTag;
	}

	public SipManager getSipManager() {
		return sipManager;
	}

	public void setSipManager(SipManager sipManager) {
		this.sipManager = sipManager;
	}

	public String getStackAddress() {
		return stackAddress;
	}

	public void setStackAddress(String stackAddress) {
		this.stackAddress = stackAddress;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public byte[] getTagBytes() {
		return tagBytes;
	}

	public void setTagBytes(byte[] tagBytes) {
		this.tagBytes = tagBytes;
	}

	public Address getToAddress() {
		return toAddress;
	}

	public void setToAddress(Address toAddress) {
		this.toAddress = toAddress;
	}

	public String getToDomain() {
		return toDomain;
	}

	public void setToDomain(String toDomain) {
		this.toDomain = toDomain;
	}

	public ToHeader getToHeader() {
		return toHeader;
	}

	public void setToHeader(ToHeader toHeader) {
		this.toHeader = toHeader;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public SipURI getURIfrom() {
		return URIfrom;
	}

	public void setURIfrom(SipURI ifrom) {
		URIfrom = ifrom;
	}

	public SipURI getURIto() {
		return URIto;
	}

	public void setURIto(SipURI ito) {
		URIto = ito;
	}

	public ViaHeader getViaHeader() {
		return viaHeader;
	}

	public void setViaHeader(ViaHeader viaHeader) {
		this.viaHeader = viaHeader;
	}

	public ArrayList getViaHeaders() {
		return viaHeaders;
	}

	public void setViaHeaders(ArrayList <ViaHeader> viaHeaders) {
		this.viaHeaders = viaHeaders;
	}

	public String getCallId() {
		return callId;
	}

	public String getFromRealName() {
		return fromRealName;
	}

	public String getRealm() {
		return realm;
	}

	public String getSipMethod() {
		return sipMethod;
	}

	public String getToRealName() {
		return toRealName;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setCallIdHeader(CallIdHeader callIdHeader) {
		this.callIdHeader = callIdHeader;
	}

	public String getIpRemote() {
		return ipRemote;
	}

	public void setIpRemote(String ipRemote) {
		this.ipRemote = ipRemote;
	}

	public int getMediaPortRemote() {
		return mediaPortRemote;
	}

	public void setMediaPortRemote(int mediaPortRemote) {
		this.mediaPortRemote = mediaPortRemote;
	}

	public String getRemoteIpContact() {
		return remoteIpContact;
	}

	public void setRemoteIpContact(String remoteIpContact) {
		this.remoteIpContact = remoteIpContact;
	}

	public int getRemotePortContact() {
		return remotePortContact;
	}

	public void setRemotePortContact(int remotePortContact) {
		this.remotePortContact = remotePortContact;
	}

	public SipURI getUriFirstLine() {
		return uriFirstLine;
	}

	public void setUriFirstLine(SipURI uriFirstLine) {
		this.uriFirstLine = uriFirstLine;
	}

	public Contact getContactHeader() {
		return contactHeader;
	}

	public void setContactHeader(Contact contactHeader) {
		this.contactHeader = contactHeader;
	}



	public Timestamp getTimeStamp() {
		return timeStamp;
	}



	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
}


