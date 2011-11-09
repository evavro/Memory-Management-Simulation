package models;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import models.process.Frame;
import models.process.Page;
import play.db.jpa.Model;

@Entity
public class MemoryState extends Model {

	// What pages belong in what frames at a certain state in memory
	public HashMap<Frame, Page> pageMap;
	
	// setup the pageMap by inserted frames 0 - n (probably should be in memory manager!)
	
	public MemoryState() {
		//save();
	}
	
	public MemoryState(final HashMap<Frame, Page> pageMap) throws Exception {
		if(pageMap.size() != Frame.count())
			throw new Exception("Cannot create a partial memory map, you must supply data for all frames in memory");
		
		// if HashMap size != Frames.count(), then all pages haven't been accounted for!
		setPageMap(pageMap);
	}
	
	public void setPageMap(final HashMap<Frame, Page> pageMap) {
		this.pageMap = pageMap;
		
		//save();
	}
	
	// getPhysicalMemoryHTML
	public String getPhysicalMemoryHTML() {
		
		return "";
	}
}
