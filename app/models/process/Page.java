package models.process;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import play.db.jpa.Model;

// Represents a temporary page in memory
@Entity
public abstract class Page extends Model {
	
	// Each page type (text/data) begins numbering its numbering with 0
	public int pageId;
	
	public int size;
	
	@ManyToOne
	public Process process;
	
	// Size of a page in bytes
	@Transient
	public final static int PAGE_MAX_SIZE = 512;
	
	public void free() {
		delete();
	}
}