package models.pages;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

// Represents a frame slot in memory
@Entity
public class Frame extends Model {
	
	// NOT the entity id
	public int id;
	
	@ManyToOne
	public Page page; // if null, frame is open
	
	public Frame(final int id) {
		this.id = id;
	}
	
	public Frame(final int id, final Page page) {
		this.page = page;
	}
}