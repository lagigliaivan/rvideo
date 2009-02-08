package client;
/**
 * First point of application execution.
 * A main window object is created and showed in the screen.
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
