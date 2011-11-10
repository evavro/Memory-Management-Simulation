package controllers;

import java.io.File;
import java.util.List;

import models.MemoryManager;
import models.process.DataPage;
import models.process.Frame;
import models.process.Process;
import models.process.TextPage;
import play.Logger;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;

public class Application extends Controller {

	// Fire up the app with the default homepage
    public static void index() {
        render();
    }
    
	public static void uploadFile(final String title, final File file) throws Exception {
		if(file == null)
			throw new Exception("No file selected for upload");
		
		MemoryManager memory = new MemoryManager(file);
		
		System.out.println(String.format("Uploaded and processed %s", file.getName()));
		
		render("Application/ProcessTable.html", memory);
	}
	
	public static void nextAction() throws Exception {
		// Retrieve the first (and hopefully only) memory manager
		MemoryManager memory = (MemoryManager) MemoryManager.findAll().get(0);
		
		memory.handleNextAction();
		memory.save();
		
		if(memory == null)
			Logger.error("Critical error, memory could not be created!");
		
		renderTemplate("Application/ProcessTable.html", memory);
	}
	
	@Before
	public static void clearData() {
		// Having issues deleting everything, JPA won't give me specific errors!
	}
	
	// Errors that occur at level lower than this controller will be caught and output here
	@Catch(Exception.class)
	public static void errorCatcher(Exception e) {
		String errorMessage = e.getMessage();
		
		e.printStackTrace();
		
		render("errors/General.html", errorMessage);
	}
}