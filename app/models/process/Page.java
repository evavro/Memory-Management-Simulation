package models.process;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;

import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
public abstract class Page extends Model {
	
	// Maximum size of a page in bytes
	@Transient
	public final static int PAGE_MAX_SIZE = 512;
	
	public abstract int getSize();
	public abstract int getFreeMemory();
	public abstract Process getProcess();
	public abstract boolean isExistent();
}