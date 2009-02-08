/**
 * Esta clase deve tener todos los metodos necesarios para
 * mostrar por pantalla en forma grafica diferentes errores, ademas
 * debe tener la capacidad de loguear informacion en diferentes archivos
 * 
 * @author Ivan
 * @version 1.0 31 Agosto 2006 
 * 
 */

package consola;
import javax.swing.JOptionPane;


public class Loguer {

	public Loguer(){}
	
	public static void showMessageError(String error){
		JOptionPane.showMessageDialog(null,error);
	}
	
	
}
