package server.src;

public class Compartido {

	private boolean fileCreated = false;
	
	public synchronized boolean getFileCreated(){
		
		while(!fileCreated){
	        try{
	            wait();
	        }catch(InterruptedException ex){}
	    }
	    fileCreated=false;
	    notify();
	    
		return fileCreated;
	}

	public synchronized void setFileCreated(){
	
		while(fileCreated){
	        try{
	            wait();
	        }catch(InterruptedException ex){}
	    }
	    fileCreated=true;
	    notify();
		
	}
	
	
	

}
