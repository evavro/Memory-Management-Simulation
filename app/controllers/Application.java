package controllers;

import java.io.File;
import java.util.List;

import models.MemoryManager;
import play.mvc.Catch;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
	public static void uploadFile(final String title, final File file) throws Exception {
		if(file == null)
			throw new Exception("No file selected for upload");
		
		// TODO: DELETE ALL PRE-EXISTING MEMORY MANAGERS
		
		MemoryManager memory = new MemoryManager(file);
		
		System.out.println(String.format("Uploaded and processed %s", file.getName()));
		
		//List<String> table = memory.getFrameTable();
		
		//render("Application/ProcessTable.html", memory);
		render("Application/ProcessTable.html", memory);
	}
	
	@Catch(Exception.class)
	public static void errorCatcher(Exception e) {
		// create and render html
	}
}