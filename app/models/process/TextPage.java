package models.process;

import javax.persistence.Entity;

@Entity
public class TextPage extends ProcessPage {
	
	public TextPage(final int id, final int size, final Process process) {
		this.pageId = id;
		this.size = size;
		this.process = process;
		
		save();
	}
	
	public Process getProcess() {
		return process;
	}
	
	@Override
	public String toString() {
		return process + " - Text page (id: " + pageId + ", size: " + size + ")";
	}
}