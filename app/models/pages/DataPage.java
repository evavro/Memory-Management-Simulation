package models.pages;

import javax.persistence.Entity;

@Entity
public class DataPage extends Page {
	
	public int pageId;
	
	public DataPage(final int id) {
		this.pageId = id;
	}
}