/**
 * First point of application execution.
 * A main window object is created and shown in the screen.
 *  
 * @author ivan
 * @version 0.1
 * @date 
 */

package client;

public class Main {
		
	public static void main(String argv[]){
	   	
		FrmPrincipal ventanaPrincipal;
		ventanaPrincipal = new FrmPrincipal();
		ventanaPrincipal.getJFrameVentanaPrincipal().setVisible(true);
	
	}

}
