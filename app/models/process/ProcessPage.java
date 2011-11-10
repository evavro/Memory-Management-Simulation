package models.process;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public abstract class ProcessPage extends Page {
	
	public int pageId;
	
	public int size;
	
	@ManyToOne
	public Process process;
	
	// Create a timestamp for this page so we can use the LRU algorithm (eventually :O)
	public Date timestamp = new Date();
	
	public int getFreeMemory() {
		return PAGE_MAX_SIZE - size;
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public boolean isExistent() {
		return true;
	}
	
	public void free() {
		delete();
	}
}
