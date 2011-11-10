package controllers;

import models.MemoryManager;
import play.mvc.Catch;
import play.mvc.Controller;

// Handles all of the requests from the HTML interface
public class ProcessController extends Application {
	
	// http://stackoverflow.com/questions/4271260/simple-ajax-with-play
	
	public static void nextAction() throws Exception {
		MemoryManager memory = (MemoryManager) MemoryManager.findAll().get(0);
		
		memory.handleNextAction();
		
		renderTemplate("Application/ProcessTable.html", memory.getFrameTableHTML());
	}
}