package controllers;

import play.*;
import play.mvc.*;

import java.io.File;
import java.util.*;

import models.*;
import models.process.ProcessFile;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
	public static void uploadFile(final String title, final File file) throws Exception {
		if(file == null)
			throw new Exception("No file selected for upload");
		
		// Parse a file and log the session
		ProcessFile session = new ProcessFile(file);
		
		System.out.println(String.format("Uploaded and processed %s", file.getName()));
		
		renderTemplate("Application/ProcessTable.html");
	}
}