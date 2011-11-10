package models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Transient;

import models.process.EmptyPage;
import models.process.Frame;
import models.process.Page;
import models.process.Process;
import play.db.jpa.Model;
import play.libs.IO;

// Parse, store and iterate through all of the data in a process sequence file
@Entity
public class MemoryManager extends Model
{
	@OneToMany(fetch = FetchType.LAZY, 
			   cascade = { CascadeType.PERSIST,
         			   	   CascadeType.REMOVE })
	public Map<Integer, Process> processes = new TreeMap<Integer, Process>();
	
	// Current mapping of memory frames and pages
	@OneToMany
	public Map<Frame, Page> pageMap = new HashMap<Frame, Page>();
	
	// List of actions in the file
	public ArrayList<String> actions;
	
	// Current action (memory state) that is being handled (current state)
	public int currentAction = 0;
	
	public String currentActionDescription = "";
	
	// The number of frames that can fit into physical memory
	public int frames = 0;
	
	// Amount of free memory provided the current state in memory
	public int freeMemory = 0;
	
	// Size of physical memory in bytes
	@Transient
	public final int MEMORY_MAX_SIZE = 4096;
	
	// The events that a process can do in memory
	public enum EventType {Arrive, Exit}
	
	public MemoryManager(final File file) throws Exception {
		
		// Delete all previous MemoryManagers and their contained objects
		this.actions = (ArrayList) IO.readLines(file);
		this.frames = MEMORY_MAX_SIZE / Page.PAGE_MAX_SIZE;
		this.freeMemory = MEMORY_MAX_SIZE;
		
		// Mark our existence so that other objects can reference us
		save();
		
		createFrames();
		createProcesses();
		
		// Start processing the first action
		handleNextAction();
	}
	
	// Create the frame slots in physical memory
	public void createFrames() {
		Frame newFrame = null;
		Page emptyPage;
		
		for(int i = 0; i < frames; i++) {
			newFrame = new Frame(i);
			emptyPage = new EmptyPage().save();
			
			pageMap.put(newFrame, emptyPage);
		}
		
		save();
	}
	
	// Create the processes
	public void createProcesses() throws Exception {
		
		for(String action : actions) {
			
			// 3 chunks in a line = process memory info
			if(splitData(action).length == 3) {
				Integer[] chunks = splitDataToInts(action);
				Integer id = chunks[0];
				Integer textSize = chunks[1];
				Integer dataSize = chunks[2];
				
				// If the process doesn't exist, create it
				if(!processes.containsKey(id))
					processes.put(id, new Process(id, textSize, dataSize, this));
			}
		}
		
		save();
	}
	
	// Interpret the next action in the file
	public void handleNextAction() throws Exception {
		String action = actions.get(currentAction);
		
		// Get the relevant process by id
		Process process = processes.get(splitDataToInts(action)[0]);
		
		if(process != null) {
			System.out.println("Current action " + currentAction + " - Process: " + process + " - Action: " + action);

			switch(determineEvent(action)) {
				case Arrive:
					currentActionDescription = String.format("%s arriving. Text page size = %s, Data page size = %s", process, process.textSize, process.dataSize);
					
					putProcessInMemory(process);
					break;
						
				case Exit:
					currentActionDescription = String.format("%s terminating.", process);
						
					terminateProcess(process);
					break;
			}
				
			currentAction++;
		}
		
		save();
	}
	
	// Places the pages of a process into frames
	public void putProcessInMemory(final Process process) {
		List<Page> pages = process.getPages();
		
		for(Page p : pages)
			allocateProcessPage(p);
	}
	
	// Terminates a process by filling the relevant frames with emnpty pages
	public void terminateProcess(final Process process) {
		for (Map.Entry<Frame, Page> slot : pageMap.entrySet()) {
			Frame frame = slot.getKey();
			Page page = slot.getValue();
			Page emptyPage = new EmptyPage().save();
			
			if(process.equals(page.getProcess()))
				pageMap.put(frame, emptyPage);
		}
	}
	
	// Memory Actions
	
	// Places a page into memory using the worst-fit algorithm
	public void allocateProcessPage(final Page page) {
		final Frame largestFrame = getLargestFrame();
		final int freeMemInFrame = getFreeBytesInFrame(largestFrame);
		
		System.out.println("Allocating page (" + page + ") @ largest frame: " + largestFrame);
		
		if(page.getSize() <= freeMemInFrame)
			pageMap.put(largestFrame, page);
	}
	
	// Find the largest frame in memory using the worst-fit algorithm)
	public Frame getLargestFrame() {
		List<Frame> frames = Frame.all().fetch();
		Frame largestFrame = frames.get(0);
		
		// Find the frame in memory with the most free memory
		for(Frame frame : frames) {
			int currFrameFreeMem = getFreeBytesInFrame(frame);
			
			// If memory is empty, the first frame is the biggest (they're all equally sized)
			if(isMemoryEmpty()) {
				largestFrame = frame;
				
				break;
			} else {
				
				// If the current frame has more free memory than the current largest, it's now the largest
				if(currFrameFreeMem > getFreeBytesInFrame(largestFrame))
					largestFrame = frame;
			}		
		}
		
		return largestFrame;
	}
	
	// Gets the amount of free memory that a frame has
	public int getFreeBytesInFrame(final Frame frame) {
		final Page matchedPage = pageMap.get(frame);
		
		return matchedPage != null ? matchedPage.getFreeMemory() : Page.PAGE_MAX_SIZE;
	}
	
	// Determines if memory is completely empty at this state in memory
	public boolean isMemoryEmpty() {
		for(Page page : pageMap.values())
			if(page.isExistent())
				return false;
				
		return true;
	}
	
	// Miscellaneous data manipulation
	
	public EventType determineEvent(final String line) throws Exception {
		final String[] chunks = splitData(line);
		final int chunkSize = chunks.length;
		
		switch(chunkSize) {
			case 3:
				return EventType.Arrive;
			case 2:
				return EventType.Exit;
			default:
				throw new Exception("File format error");
		}
	}
		
	public String[] splitData(final String line) {
		return line.split(" ");
	}
		
	public Integer[] splitDataToInts(final String line) {
		String[] chunks = splitData(line);
		Integer[] intChunks = new Integer[chunks.length];
			
		for(int i = 0; i < chunks.length; i++) {
			try {
				intChunks[i] = Integer.parseInt(chunks[i]);
			} catch (NumberFormatException e) {
				intChunks[i] = -1;
			}
		}
			
		return intChunks;
	}
		
	// HTML / Template Methods
	
	public String getFrameTableTitle() {
		return "Physical Memory (" + MEMORY_MAX_SIZE + " bytes)";
	}
	
	public String getActionDescription() {
		return currentActionDescription;
	}
	
	/*public List<ProcessPageTable> getProcessPageTables() {
		List<ProcessPageTable> pts = new ArrayList<ProcessPageTable>();
		
		for(Map.Entry<Integer, Process> p : processes.entrySet()) {
			Map<Frame, Page> memMap = new HashMap<Frame, Page>();
			Process process = p.getValue();
			List<Page> pages = process.getPages();
			
			memMap.put(pageMap.g, value)
		}
	}*/
	
	public List<String> getFrameTable() {
		List<String> framesHTML = new ArrayList<String>(frames);
		
		for(Map.Entry<Frame, Page> slot : pageMap.entrySet()) {
			Page p = slot.getValue();
			
			framesHTML.add(p.toString());
		}
		
		return framesHTML;
	}
	
	// Death and cleanup
	
	@PreRemove
	public void resetMemory() {
		delete();
		/*for(Map.Entry<Integer, Process> proc : processes) {
			proc.getValue().terminate();
		}*/
	}
	
}
