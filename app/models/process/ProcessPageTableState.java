package models.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.MemoryState;

import play.db.jpa.Model;

// A snapshot of a state of a ProcessPageTable
@Entity
public class ProcessPageTableState extends Model { //extends MemoryState {
	
	@ManyToOne
	public ProcessPageTable table; // Page table
	
	// Keeps track of how the pages are allocated to frames in memory at a given state
	@Column(insertable=false, updatable=false) // this will probably be conflictive..
	public HashMap<Frame, Page> pageMap = new HashMap<Frame, Page>();
	
	int currentState;
	
	public ProcessPageTableState(final ProcessPageTable table) {
		this.table = table;
	}
	
	public void addPageToFrame(final Frame frame, final Page page) {
		pageMap.put(frame, page);
	}
	
	// updateTextPages
	// updateDataPages
	// keep track of what frames apply to what page
	
	// This should be in the process controller maybe
	public int getCurrentState() {
		return currentState;
	}
	
	public String getHTML() {
		return "";
	}
}
