package models.process;

import javax.persistence.Entity;

// Represents the non-existence of a page in memory
@Entity
public class EmptyPage extends Page {
	
	@Override
	public int getSize() {
		return 0;
	}
	
	@Override
	public int getFreeMemory() {
		return PAGE_MAX_SIZE;
	}
	
	public Process getProcess() {
		return null;
	}

	@Override
	public boolean isExistent() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Empty";
	}
}
