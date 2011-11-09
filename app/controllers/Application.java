package controllers;

import java.io.File;

import models.MemoryManager;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
	public static void uploadFile(final String title, final File file) throws Exception {
		if(file == null)
			throw new Exception("No file selected for upload");
		
		MemoryManager session = new MemoryManager(file);
		
		System.out.println(String.format("Uploaded and processed %s", file.getName()));
		
		renderTemplate("Application/ProcessTable.html", session);
	}
}