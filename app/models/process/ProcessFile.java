package models.process;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;
import play.libs.IO;

// Parse and store all of the data in a process sequence file
@Entity
public class ProcessFile extends Model {
	
	// All the processes
	@OneToMany
	public Map<Integer, Process> processes = new TreeMap<Integer, Process>();
	
	// List of lines in the file
	public List<String> lines;
	
	// Current line that is being parsed
	public int currentLine = 0;
	
	public ProcessFile(final File file) {
		// Parse all of the lines
		//		 Process an individual line
		//				Create a ProcessTableState and add it to a collection of states in ProcessPageTable
		
		this.lines = IO.readLines(file);
		
		System.out.println("WHELHRLKEWJRLKEWJR");
		
		createProcesses();
		// createProcessStates();
	}
	
	public void createProcesses() {
		for(String line : lines) {
			Integer[] chunks = splitDataToInts(line);
			Integer id = chunks[0];
			Integer textSize = chunks[1];
			Integer dataSize = chunks[2];
			
			if(!processes.containsKey(id))
				processes.put(id, new Process(id, textSize, dataSize));
		}
		
		System.out.println("Processes: " + processes.size());
	}
	
	public void createProcessStates() {
		for(String line : lines) {
			Integer[] chunks = splitDataToInts(line);
			Integer id = chunks[0];
			Process proc = processes.get(id);
			
			// this could get messy...
		}
	}

	public void parseNextLine() {
		
		// If there's a line to parse
		if(currentLine + 1 != lines.size()) {
			
		}	
	}
	
	public void parseLine(final int lineIndex) {
		/* 	allocated and mapped 3 text pages and 2 data pages for Process 0
			allocated and mapped 2 text pages and 1 data page for Process 1
			reclaimed the frames released when process 0 terminates. */
		
		// create a ProcessPageTableState
	}
	
	// True = Enter, False = Exit
	/*private boolean determineEvent(final String line) throws Exception {
		final String[] chunks = splitData(line);
		final int chunkSize = chunks.length;
			
		if(chunkSize == 3)
			return true;
		else if(chunkSize == 2)
			return false;
		else
			throw new Exception("File format error");
	}*/
		
	private String[] splitData(final String line) {
		return line.split(" ");
	}
		
	private Integer[] splitDataToInts(final String line) {
		String[] chunks = splitData(line);
		Integer[] intChunks = new Integer[chunks.length];
			
		for(int i = 0; i < chunks.length; i++)
			intChunks[i] = Integer.parseInt(chunks[i]);
			
		return intChunks;
	}
}
