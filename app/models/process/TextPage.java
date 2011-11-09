package models.process;

import javax.persistence.Entity;

@Entity
public class TextPage extends Page {
	
	public TextPage(final int id, final int size, final Process process) {
		this.pageId = id;
		this.process = process;
		
		save();
	}
	
	@Override
	public String toString() {
		return "TextPage, db id: " + getId() + " pageId: " + pageId + " process: " + process;
	}
}