package server.src;
/**
 * En este archivo se guardan tres clases, la clase SipManager,
 * la clase SipManagerAllocator, y el hilo que refresca la asociacion en el 
 * proxy sip
 * 
 * La clase SipManager maneja todas las comunicaciones, tanto entrante como salientes.
 * Las comunicaciones salientes las maneja mediante objetos SipConexion.
 * Ademas configura y crea el stack sip donde se recibiran los mensajes
 * Instacia, factorias y demas.
 * 
 * @author ivan lagiglia
 * @version 1.0 14 de Setiembre de 2006
 *   
 */


import gov.nist.javax.sdp.MediaDescriptionImpl;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPRequest;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.media.Format;
import javax.media.MediaLocator;
import javax.sdp.Connection;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Response;

import org.apache.log4j.Logger;




public class SipManager implements SipListener	{
		
	/**
	 * Este objeto se utiliza parao crear el SipStack y el Address
	 * Message y Header Factories.
	 */
	private SipFactory sipFactory = null;
	
	/**
	 * El AddressFactory se usa para crear las URLs y los objetos Address.
	 */
	private AddressFactory addressFactory = null;

	/**
	 * El HeaderFactory se usa para crear los header SIP message.
	 */
	private HeaderFactory headerFactory = null;
	
	/**
	 * El Message Factory se usa para crear SIP messages.
	 */
	private MessageFactory messageFactory = null;
	
	/**
	 * El sipStack instancia el manejador SIP de communicaciones.
	 */
	private SipStack sipStack = null;
	
	/**
	 * El unico SIP listening point de la aplicacion
	 */
	private ListeningPoint listeningPoint = null;
	
	/**
	 * JAIN SIP SipProvider instancia.
	 */
	private SipProvider sipProvider = null;
	
	/**
	 * Se utiliza para crear los cuerpos SDP
	 */
	private SdpFactory sdpFactory = null;
	
	/**
	 * nombre por defecto de la implementacion del stack sip
	 */	
	private String sipStackPath = "gov.nist";
	
	/**
	 * Protocolo a utilizar
	 */
	private String transport = "UDP";
	
	/**
	 * Puerto que se abrira para recibir los mensajes
	 * Por default 5060
	 */
	private int localPort = 5060;
	
	/**
	 * Proxy SIP al que debemos conectarnos
	 */
	private String outboundProxy = null;
	
	/**
	 * En esta variable se almacenara la DIRECCION IP donde se recibiran 
	 * los paquetes SIP, o sea en esta direccion escuchara el objeto 
	 * listeningPoint
	 */
	private String stackAddress = null;
	
	/**
	 * Variable que almacena el nombre del stack sip utilizado 
	 */
	private String stackName = "sip-communicator";
	
	/**
	 * Ruta de paquetes donde encontrar la implementacion SIP
	 */
	private String stackPath = "gov.nist";
	
	/**
	 * Objeto properies el cual contendra la configuracion del stack SIP
	 */
	private Properties properties=new Properties();
	
	/**
	 * Objeto que atendera los paquetes SIP entrantes
	 */
	private SipListener listener = null;
	
	/**
	 * Tiempo que se espera por la respuesta a un SIP 
	 * request
	 */
	public static final int VALOR_MAX_TIMEOUT = 20; 
	
	/**
	 * Este es un hilo que refresca la asociacion URI-IP en el registrar SIP 
	 */
	private ThrBindRefresher threadRefresh = null;
	
	/**
	 * nombre de usuario y pass con el cual me logeo en el registrar sip
	 * y son inicializados cuando se envia un register. 
	 * Se utilizan para no ser pasados por parametro en cada metodo de este
	 * objeto
	 */
	private String userName = null;
	
	/**
	 * Dominio del servidor sip al que debemos registrarnos
	 */
	private String userDomain = null;
	
	/**
	 * Varible temporal en la que en algunas ocaciones se guarda
	 * el password del usuario
	 */
	private String userPass = null;
	
	/**
	 * Direccion fisico del dispositivo del cual se van a capturar datos
	 */
	private String dispositivoCaptura = null;
	
	/**
	 * direccion ip publica que obtengo a traves 
	 * del servidor STUN
	 */
	private InetAddress stunAddress = null;
	
	/**
	 * misma direccion que obtengo del servidor STUN pero en un objeto String
	 * esto se debe al error de la clase StunDiscoveryInfo que al retornar la direccion ip
	 * la retorna con una "/" delante
	 */
	private String ipPublicAddress = null;
	
	/**
	 * Este objeto se utiliza para guardar en buffers los mensajes salientes y 
	 * entrantes
	 */
	private SipConexionAllocator allocator = new SipConexionAllocator();
	
	/**
	 * Nombre del servidor stun por defecto. Si no se especifica uno
	 * se utilizara este  
	 */
	private String stunServer = "stun.xten.net";
	
	/**
	 * Puerto del servidor stun por defecto. Si no se especifica uno
	 * se utilizara este  
	 */
	private int stunServerPort = 3478;
	
	/**
	 * Este objeto se utiliza parar transmitir video mediante RTP
	 */
	private RtpTransmiter rtpTransmit;
	
	/**
	 * Este flag se utiliza en el momento de la desconexion para saber
	 * si se esta transmitiendo o no
	 */
	private boolean transmiting = false;
	
	/**
	 * Tipo de compresion que va utilizar el objeto
	 * RtpTranmiter
	 */
	private String tipoCompresion = "JPEG_RTP";
	
	/**
	 * Factor de compresion a utilizar al 
	 * enviar el video. El valor esta entre 0.1 - 0.9 
	 */
	private float factorComresion = 0.5f;
	
	private ThrKeepAliver thrKeepAliver = null;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	/** 
	 * Constructor
	 * @param protocol: protocolo a utilizar para la comunicacion con el servidor SIP, generalmente UDP
	 * @param port: puerto donde se recibiran los mensajes SIP
	 * @param stackName: alias para la implementacion sip
	 * @param stackPath: como encontrar la implementacion sip
	 */
	public SipManager(String protocol, int port,String stackName,String stackPath)
	throws UnknownHostException
	{		
			
		//puerto donde se abrira la conexion SIP
		setLocalPort(port);
		
		//tipo de protocolo a utilizar
		setTransport(protocol);
		
		//alias que se le dara a la implementacion del api sip
		setStackName(stackName);
		
		//ubicacion de la implementacion sip
		setStackPath(stackPath);
		
		setListener(this);
		
	}
	
	public SipManager(String protocol, int port, String stackAddress,
			String stackName, String stackPath) 
	throws UnknownHostException
	{		
		/**
		 *	direccion IP donde se abrira la conexion 
		 *	a esta direccion se enlazar� el SipStack
		 *  Compruebo que la direccion a setear no sea la loop, si esto es asi,
		 *  lanzo una excepcion
		 */		
		
		if(!stackAddress.contains("127.")){
			setStackAddress(stackAddress);
		}
		else{
			throw new UnknownHostException();
		}		
		//puerto donde se abrira la conexion
		setLocalPort(port);
		
		//tipo de protocolo a utilizar
		setTransport(protocol);
		
		//alias que se le dara a la implementacion del api sip
		setStackName(stackName);
		
		//ubicacion de la implementacion sip
		setStackPath(stackPath);
		
		setListener(this);
		
	}
	
	/**
	 * Inicializa el stack sip 
	 *
	 */
	private void initProperties()
	{
		setOutboundProxy(getStackAddress() + ":" + localPort + "/" + transport);
		getProperties().setProperty("javax.sip.STACK_NAME",getStackName());
		getProperties().setProperty("javax.sip.STACK_PATH",getStackPath());
		getProperties().setProperty("javax.sip.IP_ADDRESS",getStackAddress());
		getProperties().setProperty("javax.sip.OUTBOUND_PROXY",getOutboundProxy());
	}
	
	/**
	 * Este metodo inicializa la estructura de objetos sip
	 * Abre el puerto de escucha, crea factorias, setea propiedades, etc
	 * @throws ExcGeneric
	 */
	public void start() throws ExcGeneric
	{
		/*
		 * seteo las propiedades de la implementacion sip
		 * mediante un objeto properties
		 */
		initProperties();
		
		//Instancio un obj. sipFactory
		
		setSipFactory(SipFactory.getInstance());
		getSipFactory().setPathName(getSipStackPath());
		
		//a partir del objeto sipFactory creo un objeto sipStack
		
		try {
			//creo el stack sip con la properties configuradas anteriormente
			setSipStack(getSipFactory().createSipStack(getProperties()));
			setAddressFactory(getSipFactory().createAddressFactory());
			setHeaderFactory(getSipFactory().createHeaderFactory());
			setMessageFactory(getSipFactory().createMessageFactory());
		}
		catch (PeerUnavailableException ex) {
			throw new ExcGeneric(ex.getMessage(),ex);
		}
		
		/*
		 * Se utiliza este booleano para saber si se pudo o no alocar el puerto
		 * udp para escuchar mensajes SIP
		 */
		boolean portAlloc = false;
		
		do{
			//seteo el puerto y tipo de protocolo (en este caso udp) donde se escucharan los mensajes SIP	
			try {
				setListeningPoint(getSipStack().createListeningPoint(getLocalPort(),getTransport()));
			}
			catch (InvalidArgumentException invalidArgExc) {
				//si el puerto esta en uso, aumento en uno y vuelvo a intentar
				setLocalPort(getLocalPort()+1);
				initProperties();
				continue;
			}
			
			catch (TransportNotSupportedException e) {
				
				throw new ExcGeneric(
						"Transport " + transport
						+
						" is not suppported by the stack!\n Try specifying another"
						+ " transport in SipCommunicator property files.\n",
						e);
			}
			portAlloc = true;
			
		}while(!portAlloc);
		
		/* a partir del objeto sipStack creo un objeto sipProvider
		 * del cual recibire todos los mensajes sip 
		 */
		try {
			
			setSipProvider(getSipStack().createSipProvider(getListeningPoint()));
		}
		catch (ObjectInUseException ex) {
			throw new ExcGeneric("No se pudo crear las factorias!\n" + 
								 ex.getMessage(), ex);
		}
		
		//defino que el objeto sipManager (Este objeto) va a recibir los paquetes entrantes SIP
		try {
			getSipProvider().addSipListener(getListener());
		}
		catch (TooManyListenersException exc) {
			
			throw new ExcGeneric(
					"No se pudo registrar como sip listener!", exc);
		}
		//creo la factoria que me permitira generar el cuerpo SDP
		try {
			setSdpFactory(SdpFactory.getInstance());
		} catch (SdpException sdpExc) {
			throw new ExcGeneric(
					"SDP " + "Error al crear sdpFactory",
					sdpExc);
		}
	}
	
	/**
	 * Configura un objeto SipConexion con los valores necesarios para 
	 * realizar una conexion. Este metodo es utilizado unicamente
	 * por la clase SipManager
	 * @param String toUser : usuario destino 
	 * @param String toDominio : dominio del usuario destino
	 * @param String fromUser : usuario desde
	 * @param String fromDominio : dominio del usuario desde 
	 * 
	 * @return SipConexion: Este objeto ya esta listo para enviar mensajes SIP
	 * 
	 * @throws ExcGeneric: En el caso de no poder crearse un objeto SipConexion
	 * 					   se retorna una exception del tipo ExcGeneric
	 */
	private SipConexion createSipConexion(String toUser,String toDominio,String fromUser, String fromDominio) 
	throws ExcGeneric
	{
		
		String nombreUsuarioTo = toUser;
		String nombreUsuarioFrom = toDominio;
		String dominioTo = fromUser;
		String dominioFrom = fromDominio;
		SipConexion sipConexion;

		sipConexion = new SipConexion (nombreUsuarioTo,dominioTo,nombreUsuarioFrom,dominioFrom,this);
		
		if(sipConexion == null)
			throw new ExcGeneric("No se pudo crear objeto SipConexion");
		else 
			return sipConexion;
		
	}
	
	/**
	 * Crea un objeto SipConexion configurado para hacer un SIP Register
	 * @param user: usuario con el cual me voy a registrar
	 * @param pass: password de usuario
	 * @param proxySip: register sip donde se va a registrar
	 *  
	 * @return int Se retorna un entero que representa el indice donde se aloco
	 * 		   el objeto sipConexion, en los buffers del objeto allocator
	 * 
	 * @throws ExcGeneric: en el caso de producirse algun error, se genera una
	 * 					   exception 
	 */
	public int createRegisterSipConexion(String user,String proxySip,char[] pass) throws ExcGeneric{
		
		String password = new String (pass);
		int descriptor;
		String nombreUsuario = user;
		String dominio = proxySip;

		/*
		 * Guardo nombre de usuario, dominio y pass
		 * para crear conexiones posteriores sin la necesidad de pasar estos
		 * valores por parametro
		 */
		setUserName(nombreUsuario);
		setUserDomain(dominio);
		setUserPass(password);
		
		SipConexion sipConexion = createSipConexion(nombreUsuario,dominio,nombreUsuario,dominio);
		sipConexion.setUserPass(password);
		
		try{
			descriptor = allocator.allocateSipRequest(sipConexion);
		}catch(ExcGeneric excGeneric){
			throw new ExcGeneric("Error al alocar objeto sipConexion");
		}
		
		return descriptor;
	}
	
	/**
	 * Utilizo nombre de usuario,dominio y pass previamente guardado
	 * por el metodo createRegisterSipConexion(String user,char[] pass)
	 * que se usa generalmete para  sipRegister
	 * Este metodo configura un objeto sipConexion para enviar un INVITE
	 * 
	 * @return retorna un objeto SipConexion, configurado para realizar un invite 
	 */
	public int createInviteSipConexion (String toUser)
    throws ExcGeneric {
		
		String nombreUsuarioTo = null;
		String dominioTo = null;
		
		if(this.userName == null || this.userDomain == null){
			ExcGeneric exception = new ExcGeneric("Error: User name y dominio no han sido previamente configurados");
			throw exception;
			
		}		
		nombreUsuarioTo = toUser.substring(0,toUser.indexOf('@'));
		dominioTo = toUser.substring( (toUser.lastIndexOf('@')) +1 );
		
		SipConexion sipConexion= null;
		sipConexion = new SipConexion (nombreUsuarioTo,this.userName,dominioTo,this.userDomain,this);
		sipConexion.setUserPass(this.userPass);
		
		int descSipConexion = 0;
		
		sipConexion.setSipMethod("INVITE");
		
		try {
			descSipConexion = getAllocator().allocateSipRequest(sipConexion);
			getAllocator().cleanSipResponse(descSipConexion);
		} catch (ExcGeneric excGeneric) {
			throw excGeneric;
		}
		return descSipConexion;
	}

	/**
	 * Utilizo nombre de usuario,dominio y pass previamente guardado
	 * por el metodo createRegisterSipConexion(String user,char[] pass)
	 * que se usa generalmete para  sipRegister
	 * Este metodo configura un objeto sipConexion para enviar un MESSAGE
	 * 
	 * @return retorna un objeto SipConexion, configurado para realizar un invite 
	 */
	public int createMessageSipConexion(String toUser,String toDomain)
    throws ExcGeneric {
		
		
		if( userName == null || userDomain == null){
			ExcGeneric exception = new ExcGeneric("Error: User name y dominio no han sido previamente configurados");
			throw exception;
		}		
		
		SipConexion sipConexion= null;
		sipConexion = new SipConexion (toUser,this.userName,toDomain,this.userDomain,this);
		sipConexion.setUserPass(this.userPass);
		
		int descSipConexion = 0;
		
		sipConexion.setSipMethod("MESSAGE");
		
		try {
			descSipConexion = getAllocator().allocateSipRequest(sipConexion);
			getAllocator().cleanSipResponse(descSipConexion);
		} catch (ExcGeneric excGeneric) {
			throw excGeneric;
		}
		return descSipConexion;
	}
	
	/**
	 * Envia un mensaje SIP Request a traves de un objeto SipConexion
	 * 
	 * @param indexConexion: Este parametro es el descriptor del sipConexion
	 * 						 creado con algunos de los metodos create...
	 * @throws ExcGeneric: en el caso de producirse un error se retorna
	 * 					   una exception
	 */
	public void sendRegisterRequest (int indexConexion)throws ExcGeneric{
		
		int timeOut = 0;
		int reintento = 0;
		Response response = null;
		SipConexion sipConexion = null;
		
		try{
			sipConexion = getAllocator().getSipConexion(indexConexion);		
		}catch(ExcGeneric excGeneric){
			throw excGeneric;
		}
		
		/*
		 * En este bucle se pregunta si hay respuesta al envio hecho con
		 * sendRegisterRequest 
		 */
		sipConexion.sendRegisterRequest();
		
		while(true){
			
			response = null;
			
			while(timeOut < VALOR_MAX_TIMEOUT && response == null){
				try{
					response = getAllocator().getResponseForAt(indexConexion);
				}catch(ExcGeneric excGeneric){
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					timeOut++;
					continue;
				}
			}
			if(timeOut >= VALOR_MAX_TIMEOUT){		
				timeOut = 0;
				throw new ExcGeneric("No se pudo conectar con el usuario requerido");
				
			}else{
				switch(response.getStatusCode()){
				case 401:
					if(reintento<3){
						getAllocator().cleanSipResponse(indexConexion);
						sipConexion.sendRegisterWithAutentic(response);
						reintento++;
					}else{
						throw new ExcGeneric("401 - Fallo al enviar usuario y password - numero maximo de reintenos alcanzado");
					}
					break;
				case 404:
                                    throw new ExcGeneric("404 - Fallo la registracion con el servidor proxy");
                                case 487:
                                    throw new ExcGeneric("487 - Accion Cancelada");
                                    
				case 200:
                                    if(threadRefresh == null || !threadRefresh.isAlive()){
					threadRefresh = new ThrBindRefresher(getAllocator(),indexConexion,this);
					threadRefresh.start();
                                    }
					return;
				default:
					throw new ExcGeneric(response.getStatusCode() + " - Fallo la registracion con el servidor proxy");
				}
			}	
		}
	}

	/**
	 * Este metodo utiliza un objeto sipConexion para enviar un invite.
	 * En el INVITE envia contenido SDP con los puertos que se utilizaran
	 * para la transmision RTP.
	 * 
	 * @param indexConexion
	 * @throws ExcGeneric
	 */
	
	public void sendInviteRequest(int indexConexion) throws ExcGeneric{
		
		/*
		 * Variable que aumenta mientras se espera una respuesta
		 */
		int timeOut = 0;
		
		/*
		 * Cantidad de reintentos al enviar el invite
		 */
		int reintento = 0;
		
		/*
		 * En este objeto se almacena la respuesta al invite
		 */
		Response response;
						
		SessionDescription desc;
		MediaDescriptor mediaDesc = new MediaDescriptor();
		
		/*
		 * Estos sockets se utilizaran para data y control de RTP
		 */
		DatagramSocket dgSocketData = null;
		DatagramSocket dgSocketControl = null;
		
		int cont = 0;
		
		/*
		 * Este while abre dos puertos udp CONSECUTIVOS y en caso de error 
		 * intenta hacerlo 10 veces
		 */
		while(cont < 10 && dgSocketData == null && dgSocketControl == null){
			try {
				dgSocketData = new DatagramSocket();
				
				if(dgSocketData != null){
					dgSocketControl = new DatagramSocket(dgSocketData.getLocalPort() + 1);
					if(dgSocketControl != null){
						break;
					}
					else{
						dgSocketData.close();
						cont++;
						continue;
					}
				}
				else{
					cont++;
					continue;
				}
						
			} catch (SocketException sockExc) {
				if(cont >= 10){
					throw new ExcGeneric("No se pudo abrir el puerto local udp para transmision RTP");
				}else{
					cont++;
					continue;
				}
			}
		}
		/*
		 * Con esto aseguro que siempre van a estar los dos puertos abiertos
		 */
		if(dgSocketData == null || dgSocketControl == null){
			throw new ExcGeneric("No se pudo abrir el puerto local udp para transmision RTP");
		}
		
		mediaDesc.setDirIp(getStackAddress());
		mediaDesc.setInfo("Sistema de monitoreo remoto");
		mediaDesc.setMedia("audio");
		mediaDesc.setRtpPort(dgSocketData.getLocalPort());
		mediaDesc.setUserAgent("java");
		mediaDesc.setUserSession(getUserName());
		
		desc = getSessionDescription();
		
		try {
			desc.setMediaDescriptions(mediaDesc.getMediaDescritpion());
		} catch (SdpException e) {
			throw new ExcGeneric(e.getMessage());
		}
		/*
		 * Me aseguro de limpiar el buffer de respuesta para esta conexion y
		 * luego envio el invite con contenido SDP a la webcam.
		 */		
		
		try {
			getAllocator().cleanSipResponse(indexConexion);
			getAllocator().getSipConexion(indexConexion).sendInviteRequest(desc);
			
		} catch (ExcGeneric commExc) {
			throw commExc;
		}
		
		while(true){
			
			response = null;
			timeOut = 0;
			
			while(timeOut < VALOR_MAX_TIMEOUT  &&  response == null){
				try{
					response = getAllocator().getResponseForAt(indexConexion);
				}catch(ExcGeneric excGeneric){
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					timeOut++;
					continue;
				}
			}
			
			if(timeOut == VALOR_MAX_TIMEOUT){		
				timeOut = 0;
				throw new ExcGeneric("timeOut: No se pudo conectar con el usuario requerido");
				
			}else{
				switch(response.getStatusCode()){
				case 100:
					if(reintento < 5){
						getAllocator().cleanSipResponse(indexConexion);
						reintento++;
					}else{
						reintento = 0;
						throw new ExcGeneric("100: La webcam no respondio el llamado - maximo de reintentos alcanzado");
					}
					break;
					
				case 180:
					getAllocator().cleanSipResponse(indexConexion);
					timeOut++;
					
					if(timeOut == VALOR_MAX_TIMEOUT)
						throw new ExcGeneric("180: Al parecer la webcam no contesto el llamado  ");
					
					break;
				case 200:
					
					byte rawContentRemoteSessionDesc[] = response.getRawContent();
					SessionDescription remoteSessionDescription  = null;
					Vector vectorRemoteMediaDescription = null;
					String stringRemoteContentSessionDesc = new String(rawContentRemoteSessionDesc);
					Connection remoteSdpMediaConnection = null;
					String remoteAddress = null;
					int remoteMediaPort = 0;
					
					ViaHeader viaHeader = (ViaHeader)response.getHeader("Via");
					
					getAllocator().getSipConexion(indexConexion).setRemoteIpContact(viaHeader.getReceived());
					getAllocator().getSipConexion(indexConexion).setRemotePortContact(Integer.parseInt(viaHeader.getParameter("rport")));
					
					try {
						remoteSessionDescription  = getSdpFactory().createSessionDescription(stringRemoteContentSessionDesc);
						
					} catch (SdpParseException sdpParseExc) {
						throw new ExcGeneric("Error: sdp parser " + sdpParseExc.getMessage());
					}
					
					remoteSdpMediaConnection = remoteSessionDescription.getConnection();
					
					try {
						vectorRemoteMediaDescription = remoteSessionDescription.getMediaDescriptions(false);
					} catch (SdpException sdpExc) {
						throw new ExcGeneric("Error: sdp parser " + sdpExc.getMessage());
					}
					
					MediaDescriptionImpl mediaDescriptionImplRemote =(MediaDescriptionImpl) vectorRemoteMediaDescription.get(0);
					
					
					/*****************************************RTP*****************************/	
					
					try {
						remoteAddress = remoteSdpMediaConnection.getAddress();
					} catch (SdpParseException sdpExc) {
						throw new ExcGeneric("Error: sdp parser " + sdpExc.getMessage());
					}
					try {
						remoteMediaPort = mediaDescriptionImplRemote.getMedia().getMediaPort();
					} catch (SdpParseException sdpExc) {
						throw new ExcGeneric("Error: sdp parser " + sdpExc.getMessage());
					}
					getAllocator().getSipConexion(indexConexion).setIpRemote(remoteAddress);
					getAllocator().getSipConexion(indexConexion).setMediaPortRemote(remoteMediaPort);
					dgSocketControl.close();
					ThrPlayer threadMediaPlayer = new ThrPlayer(dgSocketData,remoteAddress,remoteMediaPort,this,indexConexion);
					threadMediaPlayer.start();
					
					/**
					 * Le da tiempo al ThrPlayer para que inicie
					 */
					while(!threadMediaPlayer.isConected())
						try {Thread.sleep(1000);} catch (InterruptedException e) {}
						//se conecto, por lo tanto salimos 
						
						return;
						
				case 401:
					if(reintento<3){
						getAllocator().cleanSipResponse(indexConexion);
						getAllocator().getSipConexion(indexConexion).sendRegisterWithAutentic(response);
						//sipConexion.sendRegisterWithAutentic(response);
						reintento++;
					}else{
						throw new ExcGeneric("401: Fallo al enviar usuario y password - numero maximo de reintenos alcanzado");
					}
					break;
				case 404:
					throw new ExcGeneric("404: Fallo la registracion con el servidor proxy");
					
				case 407:	
					getAllocator().cleanSipResponse(indexConexion);
					getAllocator().getSipConexion(indexConexion).incrementCSeq();
					getAllocator().getSipConexion(indexConexion).sendInviteWithAuthentic(response,desc);
					break;
				case 408:
					getAllocator().getResponseForAt(indexConexion);
					throw new ExcGeneric(" 408: Al parecer la webcam no contesto el llamado");
					
				default:
					throw new ExcGeneric("Se produjo un error al enviar el INVITE");
				}
			}	
		}
	}
	
	public void sendBy(int indexConexion)throws ExcGeneric{
		
		int timeOut = 0;
		Response response;
		SipConexion sipConexion = null;
		
		sipConexion = getAllocator().getSipConexion(indexConexion);
		getAllocator().cleanSipResponse(indexConexion);
		sipConexion.setSipMethod("BYE");
		sipConexion.sendBy();
		
		while(true){
			
			response = null;
			
			while(timeOut < VALOR_MAX_TIMEOUT && response == null){
				try{
					response = getAllocator().getResponseForAt(indexConexion);
				}catch(ExcGeneric excGeneric){
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					timeOut++;
					continue;
				}
			}
			
			if(timeOut == VALOR_MAX_TIMEOUT){		
				timeOut = 0;
				throw new ExcGeneric("timeOut: No se pudo conectar con el usuario requerido");
				
			}else{
				switch(response.getStatusCode()){
				case 407:
					getAllocator().cleanSipResponse(indexConexion);
					sipConexion.incrementCSeq();
					sipConexion.sendByWhitAuthentic(response);
					break;
				case 408:
					logger.error("Nuestro servidor no contesta el bye pero desconecta :(");
					return;
				case 200:
					logger.error("Nuestro servidor desconecto con exito :)");
					return;
				default:
					logger.error("Codigo retornado por nuestro servidor desconocido");
					return;
				}
			}
		}
	}
	
	public void sendResponseToInvite(RequestEvent requestEvent) throws ExcGeneric{
		
		SessionDescription desc;
		MediaDescriptor mediaDesc = new MediaDescriptor();
		Vector vecMediaDesc = null;
		Response response = null;
		SIPRequest request;
		
		SipURI  contactURI = null;
		Contact contactHeader = new Contact();
		
		request =(SIPRequest)requestEvent.getRequest();
		
		byte rawContentSessionDesc[] = request.getRawContent();
		String stringContentSessionDesc = new String(rawContentSessionDesc);
		SessionDescription remoteSessionDesc  = null;
		
		CallIdHeader callIdHeader = request.getCallId();
		
		javax.sip.header.CSeqHeader cSeqHeader = request.getCSeq();
		ViaList viaList = request.getViaHeaders();
		
		Address contactAddress = null;
		int ceqNumber = cSeqHeader.getSequenceNumber();
		
		try {
			cSeqHeader.setSequenceNumber(ceqNumber + 1);
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
			return;
		}
		javax.sip.header.ContentTypeHeader contentTypeHeader = request.getContentTypeHeader();
		
		MaxForwardsHeader maxForwardsHeader = request.getMaxForwards();
		
		FromHeader fromHeader = request.getFrom();
		ToHeader   toHeader =	request.getTo();
		
		/*
		 * Este while abre dos puertos udp CONSECUTIVOS y en caso de error 
		 * intenta hacerlo 10 veces
		 */
		DatagramSocket dgSocketData = null;
		DatagramSocket dgSocketControl = null;
		int cont = 0;
		while(cont < 10 && dgSocketData == null && dgSocketControl == null){
			try {
				dgSocketData = new DatagramSocket();
				
				if(dgSocketData != null){
					dgSocketControl = new DatagramSocket(dgSocketData.getLocalPort() + 1);
					if(dgSocketControl != null){
						break;
					}
					else{
						dgSocketData.close();
						cont++;
						continue;
					}
				}
				else{
					cont++;
					continue;
				}
				
			} catch (SocketException sockExc) {
				if(cont >= 10){
					throw new ExcGeneric("No se pudo abrir el puerto local udp para transmision RTP");
				}else{
					cont++;
					continue;
				}
			}
		}
		/*
		 * Con esto aseguro que siempre van a estar los dos puertos abiertos
		 */
		if(dgSocketData == null || dgSocketControl == null){
			throw new ExcGeneric("No se pudo abrir el puerto local udp para transmision RTP");
		}
			
		mediaDesc.setDirIp(getStackAddress());
		mediaDesc.setInfo("Sistema de monitoreo remoto");
		mediaDesc.setMedia("audio");
		mediaDesc.setRtpPort(dgSocketData.getLocalPort());
		mediaDesc.setUserAgent("java");
		mediaDesc.setUserSession(getUserName());
		
				
		try {
			remoteSessionDesc  = getSdpFactory().createSessionDescription(stringContentSessionDesc);
			
		} catch (SdpParseException sdpParseExc) {
			throw new ExcGeneric("Error: sdp parser " + sdpParseExc.getMessage());
		}
		Connection connection = remoteSessionDesc.getConnection();
		
		try {
			vecMediaDesc = remoteSessionDesc.getMediaDescriptions(false);
			
		} catch (SdpException sdpExc) {
			throw new ExcGeneric("Error: sdp parser " + sdpExc.getMessage());
		}
		
		MediaDescriptionImpl mediaDescImpl =(MediaDescriptionImpl) vecMediaDesc.get(0);
		
		desc = getSessionDescription();

		try {
			desc.setMediaDescriptions(mediaDesc.getMediaDescritpion());
		} catch (SdpException e1) {
			throw new ExcGeneric(e1.getMessage());
		}
		
		try {
			response = getMessageFactory().createResponse(200, callIdHeader,cSeqHeader,fromHeader, toHeader, viaList, maxForwardsHeader,contentTypeHeader,desc);
		} catch (ParseException e) {
			throw new ExcGeneric(e.getMessage());
		}
		
		try {
			contactURI = getAddressFactory().createSipURI(getUserName(),getIpPublicAddress());
		} catch (ParseException e1) {
			throw new ExcGeneric(e1.getMessage());
		}
		try {
			contactURI = getAddressFactory().createSipURI(getUserName(),getIpPublicAddress());
		} catch (ParseException e1) {
			throw new ExcGeneric(e1.getMessage());
		}
		
		contactURI.setPort(getLocalPort());
		
		contactAddress = getAddressFactory().createAddress(contactURI);
		
		contactHeader.setHeaderName("Contact");
		contactHeader.setAddress(contactAddress);
		response.addHeader(contactHeader);
		try {
			getSipProvider().sendResponse(response);
		} catch (SipException e) {
			throw new ExcGeneric(e.getMessage());
		}
		
		int localPort = dgSocketData.getLocalPort();

		/*****************************************RTP*****************************/	
		Format fmt = null;
		
		dgSocketData.close();
		dgSocketControl.close();
		
		try{
			rtpTransmit = new RtpTransmiter(new MediaLocator(getDispositivoCaptura()),
					connection.getAddress(), Integer.toString(localPort),
					mediaDescImpl.getMedia().getMediaPort(), fmt);
			
			try {
				rtpTransmit.setLocalAddress(InetAddress.getByName(getStackAddress()));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			rtpTransmit.setDestinationPort(mediaDescImpl.getMedia().getMediaPort());
			rtpTransmit.setFactorCompresion(getFactorComresion());
			rtpTransmit.setTipoCompresion(getTipoCompresion());
			
		}catch(SdpParseException parseExc){
			parseExc.printStackTrace();
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ie) {
		}
		
		String result = rtpTransmit.start();
		
		// result will be non-null if there was an error. The return
		// value is a String describing the possible error. Print it.
		if (result != null) {
			logger.error("Error : " + result);
			return;
		}
		
		logger.info("Start transmission...");
		
		String uri = fromHeader.getAddress().getURI().toString();
		String userTo =  uri.substring(4, uri.indexOf('@'));
		String domainTo = uri.substring(uri.indexOf('@') + 1);
		 
		thrKeepAliver = new ThrKeepAliver(this,userTo,domainTo,getRtpTransmit());
		thrKeepAliver.start();
		
	}
public void sendMessageRequest(int indexConexion)throws ExcGeneric{
		
		int timeOut = 0;
		Response response = null;
				
		try{
			getAllocator().getSipConexion(indexConexion).sendRequest("MESSAGE", null);		
		}catch(ExcGeneric excGeneric){
			throw excGeneric;
		}
						
		while(true){
			
			response = null;
			
			while(timeOut < VALOR_MAX_TIMEOUT && response == null){
				try{
					response = getAllocator().getResponseForAt(indexConexion);
				}catch(ExcGeneric excGeneric){
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					timeOut++;
					continue;
				}
			}
			if(timeOut == VALOR_MAX_TIMEOUT){		
				timeOut = 0;
				throw new ExcGeneric("No se pudo conectar con el usuario requerido");
				
			}else{
				switch(response.getStatusCode()){
				case 404:
					throw new ExcGeneric("404 - Fallo la registracion con el servidor proxy");
				
				case 400:
					throw new ExcGeneric("400 - " + response.getReasonPhrase());
					
				case 200:
					return;
				default:
					throw new ExcGeneric(response.getStatusCode() + " - Fallo la registracion con el servidor proxy");
				}
			}	
		}
	}
	public void sendOkResponse(RequestEvent requestEvent) throws ExcGeneric{
		
		Response response = null;
		SIPRequest request;
		
			
		request =(SIPRequest)requestEvent.getRequest();
	
		/*
		 * Este while abre dos puertos udp CONSECUTIVOS y en caso de error 
		 * intenta hacerlo 10 veces
		 */
				
		try {
			  response = getMessageFactory().createResponse(200, request);
			//response = getMessageFactory().createResponse(200, callIdHeader,cSeqHeader,fromHeader, toHeader, viaList, maxForwardsHeader,contentTypeHeader,desc);
		} catch (ParseException e) {
			throw new ExcGeneric(e.getMessage());
		}
				
		try {
			getSipProvider().sendResponse(response);
		} catch (SipException e) {
			throw new ExcGeneric(e.getMessage());
		}
		
		
		
	}
	
	/**
	 * Este metodo realiza la deteccion de la ip publica 
	 * mediante el algoritmo STUN
	 * @throws ExcGeneric
	 */
	public void stunDetection() throws ExcGeneric{
		
		StunDiscoveryTest discoveryTest = new StunDiscoveryTest(getStunServer(),getStunServerPort() );
		StunDiscoveryInfo discoveryInfo = null;
		
		try {
			discoveryInfo = discoveryTest.test();
		
		}catch(Exception exc){
			
			setIpPublicAddress(getStackAddress());
			try {
				setStunAddress(InetAddress.getByName(getStackAddress()));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			throw new ExcGeneric("Error: al detectar ip publica  - configurando ip local " + getStackAddress());
		}
		/*
		 * Hacemos esto por las dudas, seria en el caso que NO se produjera ninguna excepcion pero...
		 * discoveryTest retornara null 
		 */
		if(discoveryInfo.getPublicIP() == null){
			setIpPublicAddress(getStackAddress());
			try {
				setStunAddress(InetAddress.getByName(getStackAddress()));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			throw new ExcGeneric("Error: al detectar ip publica  - configurando ip local " + getStackAddress());
			
		}else{
			setStunAddress(discoveryInfo.getPublicIP());
			setIpPublicAddress(getStunAddress().getHostAddress());
		}
		
}
	
	
	public SessionDescription getSessionDescription(){
		
		SessionDescription sdpDesc = null;
		
		try{
			sdpDesc = sdpFactory.createSessionDescription(); 
		}
		catch(SdpException e){
			e.printStackTrace();
		}
		return sdpDesc;
	}
	
	/**
	 * Pone factorias y listeningPoint a null, para instanciarlos nuevamente
	 *
	 */
	public void reset(){
		
		try {
			getSipStack().deleteListeningPoint(getListeningPoint());
		} catch (ObjectInUseException e) {
			logger.error(e.getMessage());
		}
		setListeningPoint(null);
		setAddressFactory(null);
		setHeaderFactory(null);
		setMessageFactory(null);
		setSipProvider(null);
	}
	
	public  void processResponse(ResponseEvent event){
		logger.info(" <-- RESPONSE: \n " + event.getResponse().getReasonPhrase());
		allocator.SetResponse(event);
	}
	
	@SuppressWarnings("deprecation")
	public  void processRequest(RequestEvent event){
		
		logger.info(" <-- REQUEST: \n " + event.getRequest().getMethod());
		
		if (event.getRequest().getMethod().compareTo("BYE") == 0 && getRtpTransmit() != null){
			getRtpTransmit().stop();
			getThrKeepAliver().stop();
			
			transmiting = false;
			try {
				sendOkResponse(event);
			} catch (ExcGeneric e) {
				logger.error("Error: Se produjo un error al contestar el BYE");
			}
			return;
		}
		if(event.getRequest().getMethod().compareTo("INVITE") == 0){
		
			if(!transmiting){
				try {
					sendResponseToInvite(event);
					transmiting = true;
				} catch (ExcGeneric e) {
					e.printStackTrace();
				}
			}	
			return;
		}
		//if (event.getRequest().getMethod().compareTo("MESSAGE") == 0 && getRtpTransmit() != null){
		  if (event.getRequest().getMethod().compareTo("MESSAGE") == 0){
			try {
				sendOkResponse(event);
			} catch (ExcGeneric e) {
				logger.error("Error: Se produjo un error al contestar el BYE");
			}
		}
 }
	
	public  void processTimeout(TimeoutEvent event){}
	
	public InetAddress getStunAddress() {
		return stunAddress;
	}
	
	public void setStunAddress(InetAddress stunAddress) {
		this.stunAddress = stunAddress;
	}
	
	public String getStackAddress() {
		return stackAddress;
	}
	
	public void setStackAddress(String stackAddress) {
		this.stackAddress = stackAddress;
	}
	
	public int getLocalPort() {
		return localPort;
	}
	
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	public AddressFactory getAddressFactory() {
		return addressFactory;
	}
	
	public void setAddressFactory(AddressFactory addressFactory) {
		this.addressFactory = addressFactory;
	}
	
	public HeaderFactory getHeaderFactory() {
		return headerFactory;
	}
	
	public void setHeaderFactory(HeaderFactory headerFactory) {
		this.headerFactory = headerFactory;
	}
	
	public MessageFactory getMessageFactory() {
		return messageFactory;
	}
	
	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	public SdpFactory getSdpFactory() {
		return sdpFactory;
	}
	
	public void setSdpFactory(SdpFactory sdpFactory) {
		this.sdpFactory = sdpFactory;
	}
	
	public SipFactory getSipFactory() {
		return sipFactory;
	}
	
	public void setSipFactory(SipFactory sipFactory) {
		this.sipFactory = sipFactory;
	}
	
	public SipProvider getSipProvider() {
		return sipProvider;
	}
	
	public void setSipProvider(SipProvider sipProvider) {
		this.sipProvider = sipProvider;
	}
	
	public String getIpPublicAddress() {
		return ipPublicAddress;
	}
	
	public void setIpPublicAddress(String ipPublicAddress){
		this.ipPublicAddress = ipPublicAddress;
	}
	
	public SipStack getSipStack() {
		return sipStack;
	}
	
	public void setSipStack(SipStack sipStack) {
		this.sipStack = sipStack;
	}
	
	public String getSipStackPath() {
		return sipStackPath;
	}
	
	public void setSipStackPath(String sipStackPath) {
		this.sipStackPath = sipStackPath;
	}
	
	public ListeningPoint getListeningPoint() {
		return listeningPoint;
	}
	
	public void setListeningPoint(ListeningPoint listeningPoint) {
		this.listeningPoint = listeningPoint;
	}
	
	public SipListener getListener() {
		return listener;
	}
	
	public void setListener(SipListener listener) {
		this.listener = listener;
	}
	
	public String getTransport() {
		return transport;
	}
	
	public void setTransport(String transport) {
		this.transport = transport;
	}
	
	public String getStackName() {
		return stackName;
	}
	
	public void setStackName(String stackName) {
		this.stackName = stackName;
	}
	
	public String getStackPath() {
		return stackPath;
	}
	
	public void setStackPath(String stackPath) {
		this.stackPath = stackPath;
	}
	
	public String getOutboundProxy() {
		return outboundProxy;
	}
	
	public void setOutboundProxy(String outboundProxy) {
		this.outboundProxy = outboundProxy;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public String getUserDomain() {
		return userDomain;
	}
	
	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserPass() {
		return userPass;
	}
	
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	
	public SipConexionAllocator getAllocator() {
		return allocator;
	}
	
	public void setAllocator(SipConexionAllocator allocator) {
		this.allocator = allocator;
	}
	
	public String getDispositivoCaptura() {
		return dispositivoCaptura;
	}
	
	public void setDispositivoCaptura(String dispositivoCaptura) {
		this.dispositivoCaptura = dispositivoCaptura;
	}
	
	public String getStunServer() {
		return stunServer;
	}
	
	public void setStunServer(String stunServer) {
		this.stunServer = stunServer;
	}
	
	public int getStunServerPort() {
		return stunServerPort;
	}
	
	public void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
	}

	public RtpTransmiter getRtpTransmit() {
		return rtpTransmit;
	}

	public void setRtpTransmit(RtpTransmiter rtpTransmit) {
		this.rtpTransmit = rtpTransmit;
	}

	public float getFactorComresion() {
		return factorComresion;
	}

	public String getTipoCompresion() {
		return tipoCompresion;
	}

	public void setFactorComresion(float factorComresion) {
		this.factorComresion = factorComresion;
	}

	public void setTipoCompresion(String tipoCompresion) {
		this.tipoCompresion = tipoCompresion;
	}

	public boolean isTransmiting() {
		return transmiting;
	}

	public void setTransmiting(boolean transmiting) {
		this.transmiting = transmiting;
	}

	public ThrKeepAliver getThrKeepAliver() {
		return thrKeepAliver;
	}

	public void setThrKeepAliver(ThrKeepAliver thrKeepAliver) {
		this.thrKeepAliver = thrKeepAliver;
	}

   	
}

/**
 * Esta clase almacena en dos vectores (vectorSipRequest y vectorSipResponse) los objetos 
 * sipConexion correspondientes a mensajes salientes y las respuestas a estos mensajes
 * @author ivan
 *
 */
class SipConexionAllocator {
	
	//En este buffer deberian guardarse todas los mensajes Request salientes
	private Vector <SipConexion> vectorSipRequest = new Vector<SipConexion>(1,1);
	//En este buffer deberian guardarse todas los mensajes Request entrantes
	private Vector <Response> vectorSipResponse = new Vector<Response>(1,1);
	
	/**
	 * Este metodo guarda en el buffer vectorSipRequest un objeto SipConexion
	 * ya configurado para hacer una conexion
	 * @param sipConexion: objeto a guardar en el vector
	 * @return: retorna el indice donde se encuentra el objeto guardado
	 */
	
	public int allocateSipRequest(SipConexion sipConexion)throws ExcGeneric{
		
		if(sipConexion == null)
			throw new ExcGeneric("No se puede allocar un objeto null");
		
		getVectorSipRequest().add(sipConexion);
		return getVectorSipRequest().indexOf(sipConexion);
		
	}
	/**
	 * Este metodo agrega en el buffer de llegada la respuesta a un mensaje
	 * siempre y cuando coincida con algun mensaje saliento almacenado
	 * en vectorSipRequest.
	 * @param event: es el evento que se genero al llegar el mensaje
	 * 				 atendido por processResponse en el objeto SipManager
	 */
	public void SetResponse(ResponseEvent event){
		
		String callId = null;
		Response response = event.getResponse();
		CallIdHeader callIdHeader;
		SipConexion request;
		/**
		 * recorro el vector request el cual contiene todos los mensajes salientes
		 * buscando el pedido a esta respuesta comparando Call-ID
		 * si encuentro que el mensaje entrante es respuesta de un mensaje saliente
		 * obtengo el indice y lo coloco con el mismo indice en el vector Response, sino
		 * lo descarto
		 */ 
		for(int i = 0; i< getVectorSipRequest().size(); i++){
			
			callIdHeader = (CallIdHeader)response.getHeader("Call-ID");
			request = (SipConexion)getVectorSipRequest().get(i);
			
			if(request == null)
				continue;
			
			callId = callIdHeader.getCallId();
			
			if(callId.compareTo(request.getCallIdHeader().getCallId()) == 0)
				getVectorSipResponse().add(i,response);
			
		}
	}
	/**
	 * Este metodo retorna el objeto response correspondiente al mensaje sip enviado
	 * mediante un objeto SipConexion, el cual es referenciado mendiante su descritpor o 
	 * indice en el vector sipRequest
	 * @param descriptorSipConexion: valor retornado por el metodo allocateSipConexion
	 * @return
	 */
	public Response getResponseForAt(int descriptorSipConexion)throws ExcGeneric{
		
		Response response;
		
		/**
		 * si el descriptor es mayor al tama�o del vector o menor a 0 retorno una execpcion
		 */
		if(descriptorSipConexion >= getVectorSipResponse().size() || descriptorSipConexion < 0)
			throw new ExcGeneric("No se encontro respuesta para esta conexion");
		else	
			response = getVectorSipResponse().get(descriptorSipConexion);
		
		/**
		 *  si al obtener el objeto response correspondiente a este vector retorna null
		 *  por lo tanto no existe respuesta para este mensaje y retorno una exception
		 */ 
		if(response == null)
			throw new ExcGeneric("No se encontro respuesta para esta conexion");
		
		return response; 
	}
	
	public SipConexion getSipConexion(int descriptorSipConexion) throws ExcGeneric{
		
		SipConexion sipConexion = null;
		
		if(descriptorSipConexion > getVectorSipRequest().size() || descriptorSipConexion < 0)
			throw new ExcGeneric("No se encontro un objeto SipConexion para el descriptor: " + descriptorSipConexion);
		else	
			sipConexion = getVectorSipRequest().get(descriptorSipConexion);
		
		if(sipConexion == null)
			throw new ExcGeneric("No se encontro un objeto SipConexion para el descriptor: " + descriptorSipConexion);
		
		return sipConexion;
	}
	/**
	 * Este metodo limpia del buffer de entrada la respuesta correspondiente al request
	 * dado por el descriptor
	 * @param descriptor
	 */
	
	public void cleanSipResponse(int descriptorSipConexion) throws ExcGeneric{
		if(descriptorSipConexion >= getVectorSipResponse().size() || descriptorSipConexion < 0)
			throw new ExcGeneric("No se encontro respuesta para esta conexion");
		else	
			getVectorSipResponse().add(descriptorSipConexion, null);
	}
	
	private Vector<SipConexion> getVectorSipRequest() {
		return vectorSipRequest;
	}
	
	private Vector<Response> getVectorSipResponse() {
		return vectorSipResponse;
	}
	
}


class ThrBindRefresher extends Thread {
	
	//private SipConexion sipConexion;
	private Response response;
	private Contact contactHeader;
	private int expires = 0;
	private int sipConexionIndex = 0;
	private final int TIME_OUT = 10;
	private int timeOut = 0; 
	private SipManager sipManager;
	private SipConexionAllocator allocator;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ThrBindRefresher (SipConexionAllocator allocator, int index , SipManager manager)
	throws ExcGeneric
	{
		
		if(manager != null && allocator != null){
			sipConexionIndex = index;
			sipManager = manager;
			this.allocator = allocator;
		}else
			throw new ExcGeneric("error: El parametro sipconexion no debe ser null");
	}
	
	public void run () {
		
		timeOut = 0;
		
		while(true){
			try {
                                logger.info("Registrando");
				response = allocator.getResponseForAt(sipConexionIndex);
			} catch (ExcGeneric excGeneric) {
				if(timeOut>= TIME_OUT){
					logger.error("Error al refrescar el binding: timeout = " +  TIME_OUT);
					while(true){
						try {
							sipManager.sendRegisterRequest(sipConexionIndex);
						} catch (ExcGeneric e) {
							logger.error("Se produjo un error en el hilo que refresca el binding: " + e.getMessage());
							try {Thread.sleep(60000);} catch (InterruptedException e1) {e1.printStackTrace();}
							continue;
						}
						break;
					}
					timeOut = 0;
					//break;
					continue;
				}
				else{
					try{Thread.sleep(2000);}catch(InterruptedException excInterrupt){logger.error("Error en el hilo refresher");};
					timeOut++; 
					continue;
				}
			}
			contactHeader = (Contact)response.getHeader("Contact");
                        expires = contactHeader.getExpires();
                        
                        if(expires > 600){
                            expires = 600 * 1000 ;
                            //expires = (expires * 1000)  /  2 ;
                        }else{
                            expires = expires * 1000;
                        }
                      logger.info("El binding expira en seg: " +  (expires / 1000) );
                      
                      try {
                           Thread.sleep(expires);
                       } catch(InterruptedException excInterrupt) {
                           logger.error("ERROR");
                       }
                 		
			try {
				allocator.cleanSipResponse(sipConexionIndex);
			} catch (ExcGeneric excGeneric) {
				logger.error(excGeneric.getMessage());
			}
			
			while(true){
				try {
					sipManager.sendRegisterRequest(sipConexionIndex);
				} catch (ExcGeneric e) {
					logger.error("Se produjo un error en el hilo que refresca el binding");
					try {Thread.sleep(60000);} catch (InterruptedException e1) {e1.printStackTrace();}
					continue;
				}
				break;
			}
			
		}
		
	}
	
}


