package models.pages;

import javax.persistence.Entity;

@Entity
public class TextPage extends Page {
	
	public int id;
	
	public TextPage(final int id) {
		this.id = id;
	}

	@Override
	public int getPageId() {
		return id;
	}
}