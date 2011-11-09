package models.process;

import javax.persistence.Entity;

@Entity
public class DataPage extends Page {
	
	public DataPage(final int id, final int size, final Process process) {
		this.pageId = id;
		this.process = process;
		
		save();
	}
	
	@Override
	public String toString() {
		return "DataPage, db id: " + getId() + " pageId: " + pageId + " process: " + process;
	}
}