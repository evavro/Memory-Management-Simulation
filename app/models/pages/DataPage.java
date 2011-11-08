package models.pages;

import javax.persistence.Entity;

@Entity
public class DataPage extends Page {
	
	public int id;
	
	public DataPage(final int id) {
		this.id = id;
	}

	@Override
	public int getPageId() {
		return id;
	}
}