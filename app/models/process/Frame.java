package models.process;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

// Represents a frame slot in memory
@Entity
public class Frame extends Model {
	
	public int frameId;
	
	public Frame(final int frameId) {
		this.frameId = frameId;
		
		save();
	}
}