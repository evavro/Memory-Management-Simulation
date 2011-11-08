package models.pages;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

// Represents a temporary page (part of a state) in memory
public abstract class Page extends Model {
	
	// each page type (text/data) begins numbering its pages with page 0
	public int pageId;
	
	@OneToOne
	public Frame frame;
	
	public void free() {
		delete();
	}
}