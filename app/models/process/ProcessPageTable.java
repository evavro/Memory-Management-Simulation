package models.process;

import java.util.ArrayList;

// PageTable
public class ProcessPageTable {

	public ArrayList<Process> processes = new ArrayList<Process>();
	
	// track Text and Data pages
	
	/* 	Page table(s) 
		Process 1: 
           		   Page    Frame 
		Text       0          5 
              	   1          6 
		Data       0          7 */
	
	public void addProcess(final Process p) {
		processes.add(p);
	}
	
	public String generateHTMLNext() {
		return "";
	}
	
	public String generateHTMLPrevious() {
		return "";
	}
	
	public int getFreeFrames() {
		return 0;
	}
	
	// Return the HTML representation of this process table
	public String getHTML() {
		return "";
	}
}
