package models.process;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

import models.pages.Page;

@Entity
public class ProcessPageTableState extends Model {
	
	// Pages at the current state of the process table
	@OneToMany
	public List<Page> pages = new ArrayList<Page>();
	
	@ManyToOne
	public ProcessPageTable table; // Page table
	
	int currentState;
	
	public ProcessPageTableState(final ProcessPageTable table) {
		this.table = table;
	}
	
	// This should be in the process controller maybe
	public int getCurrentState() {
		return currentState;
	}
	
	public String getHTML() {
		return "";
	}
}
