package models.process;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;


import play.db.jpa.Model;

@Entity
public class ProcessPageTable extends Model {

	// Relevant process
	@OneToOne
	public Process process;
	
	// Various states of the page table defined in the source file
	@OneToMany
	public List<ProcessPageTableState> states = new ArrayList<ProcessPageTableState>();
	
	// Track Text and Data pages
	
	/* 	Page table(s) 
		Process 1: 
           		   Page    Frame 
		Text       0          5 
              	   1          6 
		Data       0          7 */
			
	public ProcessPageTable(final Process process) {
		this.process = process;
	}
	
	public void addState(final ProcessPageTableState state) {
		states.add(state);
	}
	
	// also in ProcessPageTableState, not sure where's better
	/*public int getFreeFrames() {
		return 0;
	}*/
	
	public String generateHTMLNext() {
		return "";
	}
	
	public String generateHTMLPrevious() {
		return "";
	}
	
	// Return the HTML representation of this process table at a specific state
	public String getHTML(final int stateIndex) {
		ProcessPageTableState state = states.get(stateIndex);
		
		return "";
	}
}
