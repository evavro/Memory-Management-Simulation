package models.pages;

import javax.persistence.Entity;

@Entity
public class TextPage extends Page {
	
	public int pageId;
	
	public TextPage(final int id) {
		this.pageId = pageId;
	}
}