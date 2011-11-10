package controllers;

import models.MemoryManager;
import play.Logger;
import play.mvc.Catch;
import play.mvc.Controller;

// Handles all of the requests from the HTML interface
public class ProcessController extends Controller {
	
	// http://stackoverflow.com/questions/4271260/simple-ajax-with-play
	
	public static void nextAction() throws Exception {
		System.out.println("\n\n\n\n\nSUP DUDE, " + MemoryManager.count());
		
		MemoryManager memory = (MemoryManager) MemoryManager.findAll().get(0);
		
		memory.handleNextAction();
		memory.save();
		
		if(memory == null)
			Logger.error("Critical error, memory could not be created!");
		
		renderTemplate("Application/ProcessTable.html", memory);
	}
}