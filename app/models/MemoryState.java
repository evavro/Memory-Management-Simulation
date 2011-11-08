package models;

import java.util.HashMap;

import models.pages.Frame;
import models.pages.Page;
import play.db.jpa.Model;

public class MemoryState extends Model {

	// Keeps track of what pages belong in what frames
	public HashMap<Frame, Page> pageMap = new HashMap<Frame, Page>();
	
	// setup the pageMap by inserted frames 0 - n (probably should be in memory manager!)
	
	// getPhysicalMemoryHTML
	public String getPhysicalMemoryHTML() {
		
		return "";
	}
}
