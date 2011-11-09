package models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.process.DataPage;
import models.process.Frame;
import models.process.Page;
import models.process.Process;
import models.process.TextPage;

import play.db.jpa.Model;
import play.libs.IO;

// Parse, store and iterate through all of the data in a process sequence file
@Entity
public class MemoryManager extends Model
{
	public TreeMap<Integer, Process> processes = new TreeMap<Integer, Process>();
	
	// List of actions in the file
	public ArrayList<String> actions;
	
	// Current action that is being handled (current state)
	public int currentAction = 0;
	
	// Size of physical memory in bytes
	public final int MEMORY_MAX_SIZE = 4096;
	
	// The number of frames that can fit into physical memory
	public final int FRAMES = MEMORY_MAX_SIZE / Page.PAGE_MAX_SIZE;
	
	// The events that a process can do in memory
	public enum EventType {Arrive, Exit}
	
	// Amount of free memory given the current state
	public int freeMemory = MEMORY_MAX_SIZE;
	
	public MemoryManager(final File file) throws Exception
	{
		this.actions = (ArrayList) IO.readLines(file);
		
		// Mark our existence so that other objects can reference us
		save();
		
		createFrames();
		createProcesses();
		// createProcessStates();
		
		// FIXME: NOT the right place for this, only for testing
		createMemoryMapping();
	}
	
	// Create the frame slots in physical memory
	public void createFrames()
	{
		for(int i = 0; i < FRAMES; i++)
			new Frame(i).save();
	}
	
	// Create the processes
	public void createProcesses() throws Exception
	{
		for(String action : actions)
		{
			// 3 chunks in a line = process memory info
			if(splitData(action).length == 3)
			{
				Integer[] chunks = splitDataToInts(action);
				Integer id = chunks[0];
				Integer textSize = chunks[1];
				Integer dataSize = chunks[2];
				
				// If the process doesn't exist, create it
				if(!processes.containsKey(id))
					processes.put(id, new Process(id, textSize, dataSize, this));
			}
		}
		
		System.out.println("Processes: " + processes.size());
	}
	
	public void createProcessStates() throws Exception 
	{
		for(String action : actions)
		{
			Integer[] chunks = splitDataToInts(action);
			Integer id = chunks[0];
			EventType type = determineEvent(action);
			Process proc = processes.get(id);
			
			// this could get messy...
			
			// go through each line, read what the action is
			// 		go through each process -> process table
			//				determine the frame/page map at the current action for a process and save it to a ProcessPageTableState
			
			switch(type) {
				case Arrive:
					
					break;
				case Exit:
					
					break;
					
				// either way, update PageTables for ALL processes whenever anything happens
			}
		}
	}
	
	// Create a map of memory (at a certain step)
	public Map<Frame, Process> createMemoryMapping() throws Exception
	{
		final Map<Frame, Process> map = new HashMap<Frame, Process>();
		final List<Frame> frames = Frame.findAll();
		final List<Process> processes = Process.findAll();
		final EventType action = determineEvent(actions.get(currentAction));
		
		// Create a snapshot of the resulting memory state
		final MemoryState memState = new MemoryState();
		
		/* Worst Fit Algorithm
		 * 
		 * The memory manager places the process in the largest block of unallocated
		 * memory available. The idea is that this placement will create the largest
		 * hole after the allocations, thus increasing the possibility that, compared
		 * to the best fit, another process can use the hole created as a result of
		 * external fragmentation.
		 */
		
		// look for the largest block of open memory
		
		// !!!! Look up the MemoryState previous to the current one and use it as a starting point
		
		for(Process process : processes) {
			List<Page> pages = process.getPages();
			
			for(Page page : pages)
				putPageInMemory(page);
		}
		
		// Create a MemoryState with the map!
		
		return null;
	}
	
	public void putPageInMemory(final Page page) {
		
	}

	public void parseNextAction()
	{
		// If there's a line to parse
		if(currentAction + 1 != actions.size()) {
			
		}	
	}
	
	public void parseAction(final int actionIndex)
	{
		/* 	allocated and mapped 3 text pages and 2 data pages for Process 0
			allocated and mapped 2 text pages and 1 data page for Process 1
			reclaimed the frames released when process 0 terminates. */
		
		// create a ProcessPageTableState
	}
	
	public EventType determineEvent(final String line) throws Exception
	{
		final String[] chunks = splitData(line);
		final int chunkSize = chunks.length;
		
		switch(chunkSize)
		{
			case 3:
				return EventType.Arrive;
			case 2:
				return EventType.Exit;
			default:
				throw new Exception("File format error");
		}
	}
		
	public String[] splitData(final String line)
	{
		return line.split(" ");
	}
		
	public Integer[] splitDataToInts(final String line)
	{
		String[] chunks = splitData(line);
		Integer[] intChunks = new Integer[chunks.length];
			
		for(int i = 0; i < chunks.length; i++)
			intChunks[i] = Integer.parseInt(chunks[i]);
			
		return intChunks;
	}
	
	// Memory Actions
	
	// Return true if malloc was successful, false otherwise
	public boolean malloc(final int bytes) throws Exception {
		final int newMemSize = freeMemory - bytes;
		
		if(newMemSize < 0)
			return false; // TODO: If this fails, move other stuff out of memory (maybe handle this action in MemoryManager?)
			
		freeMemory = newMemSize;
		
		System.out.println("Allocated " + bytes + " bytes");
		
		//save(); - causes issues, not even sure if necessary yet
		
		return true;
	}
	
	// HTML Methods
}
