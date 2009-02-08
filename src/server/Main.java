/**
 * Punto de entrada a la aplicacion. Aqui se lee un archivo
 * de configuracion xml en el cual se especifican la configuracion
 * necesaria para que la app comience su ejecucion
 * @author ivan
 * @version 1.0 19 Setiembre 2006
 * 
 * TODO avoid hardcoding config file. It should be able to pass it as a parameter.
 */

package server;
import java.io.File;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Main {

	//objeto que se utiliza para leer configuracion de un archivo xml
	private XmlConfigParser parserConf      = null;
	private final String fileName		    = System.getProperty("user.dir") + File.separator + "config.xml";
	private SipManager sipManager		    = null;
	private int sipConexionRegisterIndex    = 0;
	private final String STACK_PATH		    = "gov.nist";
	private final String STACK_PATH_NAME	= "gov.nist";
	private int reintento			        = 0;
	private Logger logger                   = Logger.getLogger(this.getClass().getName());
    private final int MAX_NUMBER_RETRY      = 2;
	
	public static void main(String[] args) {

		Main sipAgentCam = new Main();
		sipAgentCam.start();
	}

	private void start(){
		
		setParserConf(new XmlConfigParser(fileName));
		/*TODO avoid using break into a while loop. 
		  there should be a variable into the parse class which
		  tell us its state.
		*/
		while(reintento < MAX_NUMBER_RETRY) {
			try {
				getParserConf().parse();
				break;
			}catch (ExcGeneric e) {
				showFrmConfig(e.getMessage());
				reintento++;
			}
		}
                
		if(reintento < MAX_NUMBER_RETRY) {
			PropertyConfigurator.configure(getClass().getResource("/config/log4j.properties"));	
			register();
        }else {
            logger.error("No se encontro archivo config.xml");
            System.exit(1);
        }
                
	}
	
	private void showFrmConfig(String error){

		FrmConfig frmConfig   = null;
		Compartido compartido = new Compartido();

		Loguer.showMessageError(error);

		/*el objeto compartido sirve de sincronismo
		 * ya que este metodo no continua hasta que la ventana de configuracion
		 * haya desaparecido 
		 */
		frmConfig = new FrmConfig(compartido);
		frmConfig.getJFrameConfig().setVisible(true);

		//spero a que se cree el archivo de configuracion DURMIENDO en compartido
		while(compartido.getFileCreated() == true);
		//elimino referencia del objeto y llamo al recolector de basura
		frmConfig = null;
		
	}

	private void register(){
		/**
		 * instancio Objeto SipManager que va a manejar todas las conexiones
		 * salientes y entrantes
		 */
		logger.info("iniciando aplicacion - iniciando sipManager");
		if(sipManager == null){
			try {
				sipManager = new SipManager("udp",
											getParserConf().getLocalHostPort(),
											getParserConf().getLocalHostAddress(),
											STACK_PATH_NAME,
											STACK_PATH);
			} catch (UnknownHostException e) {
				logger.error(e);
				Loguer.showMessageError(e.getMessage());
				System.exit(1);
			}
		}
		sipManager.setDispositivoCaptura(getParserConf().getDispositivo());
		sipManager.setTipoCompresion(getParserConf().getTipoCompresion());
		sipManager.setFactorComresion(Float.parseFloat(getParserConf().getFactorCompresion()));
		try {
			logger.info("stun deteccion");
			sipManager.stunDetection();
		} catch (ExcGeneric commEx) {
			logger.error(commEx);
		}
		try {
			sipManager.start();
		} catch (ExcGeneric e1) {
			logger.error(e1);
			//Loguer.showMessageError(e1.getMessage());
			return;
		}
		try {
			String passDecript = getParserConf().getPassword();
			sipConexionRegisterIndex = sipManager.createRegisterSipConexion(getParserConf().getUsuario(),
					getParserConf().getSipProxyAddress(),
					passDecript.toCharArray()												
			);
			sipManager.sendRegisterRequest(sipConexionRegisterIndex);
		} catch (ExcGeneric e) {
			logger.error(e);
			System.exit(1);
		} 

		
	}

	private XmlConfigParser getParserConf() {
		return parserConf;
	}

	private void setParserConf(XmlConfigParser parserConf) {
		this.parserConf = parserConf;
	}

}
