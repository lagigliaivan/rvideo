package consola;
/**
 * Punto de entrada para la aplicacion
 * Instancia un objeto ventanaPrincipal y lo muestra
 * 
 * @author ivan
 * @version 0.1 
 */

public class Main {
		
	public static void main(String argv[]){
	   	
		FrmPrincipal ventanaPrincipal;
		ventanaPrincipal = new FrmPrincipal();
		ventanaPrincipal.getJFrameVentanaPrincipal().setVisible(true);
	
	}

}
