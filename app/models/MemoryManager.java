package models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.process.EmptyPage;
import models.process.Frame;
import models.process.Page;
import models.process.Process;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import play.libs.IO;

// Parse, store and iterate through all of the data in a process sequence file
@Entity
public class MemoryManager extends Model
{
	@OneToMany
	public Map<Integer, Process> processes = new TreeMap<Integer, Process>();
	
	// List of actions in the file
	public ArrayList<String> actions;
	
	@OneToMany
	public Map<Frame, Page> pageMap = new HashMap<Frame, Page>();
	
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
	
	@Transient
	private EntityManager em = JPA.em();
	
	public MemoryManager(final File file) throws Exception {
		
		// Delete all previous MemoryManagers and their contained objects
		this.actions = (ArrayList) IO.readLines(file);
		this.frames = MEMORY_MAX_SIZE / Page.PAGE_MAX_SIZE;
		this.freeMemory = MEMORY_MAX_SIZE;
		
		// Mark our existence so that other objects can reference us
		save();
		
		createFrames();
		//save(); // GREAT SUCCESS, JUST LOOK FOR THINGS IN createProcesses THAT DON'T GET SAVED AND WE'RE GOOD TO GO
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
		
		System.out.println("Processes: " + processes.size());
	}
	
	public void handleNextAction() throws Exception {
		String action = actions.get(currentAction);
		Process process = processes.get(splitDataToInts(action)[0]);
		
		if(process != null) {
			System.out.println("Current action " + currentAction + " - Process: " + process + " - Action: " + action);
				
				// Relate the manager with a memory state
				//memoryStates.add(memState);
				
				// go through each line, read what the action is
				// 		go through each process -> process table
				//				determine the frame/page map at the current action for a process and save it to a ProcessPageTableState
				
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
			
			//em.persist(this);
		}
		
		save();
	}
	
	public void putProcessInMemory(final Process process) {
		List<Page> pages = process.getPages();
		
		for(Page p : pages)
			allocateProcessPage(p);
				
		//em.persist(this);
				
		/*System.out.println("\n\n&&&& Just finished putting process " + process + " into memory. Here's the table:");
				
		for(Map.Entry<Frame, Page> entry : pageMap.entrySet()) {
			System.out.println("Frame " + entry.getKey() + " : Page " + entry.getValue());
		}*/
				
		
	}
	
	public void terminateProcess(final Process process) {
		for (Map.Entry<Frame, Page> slot : pageMap.entrySet()) {
			Frame frame = slot.getKey();
			Page page = slot.getValue();
			Page emptyPage = new EmptyPage().save();
			
			if(process.equals(page.getProcess()))
				pageMap.put(frame, emptyPage);
		}
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
			
		for(int i = 0; i < chunks.length; i++) {
			try {
				intChunks[i] = Integer.parseInt(chunks[i]);
			} catch (NumberFormatException e) {
				intChunks[i] = -1;
			}
		}
			
		return intChunks;
	}
	
	// Memory Actions
	
	// **** Definitely neeed to build this map from the individual process pcbs....
	
	// Places a page into memory using the worst-fit algorithm
	public void allocateProcessPage(final Page page) {
		final Frame largestFrame = getLargestFrame();
		final int freeMemInFrame = getFreeBytesInFrame(largestFrame);
		
		System.out.println("ENTERING PAGE MAP: " + pageMap);
		System.out.println("Allocating page (" + page + ") @ largest frame: " + largestFrame);
		
		if(page.getSize() <= freeMemInFrame)
			pageMap.put(largestFrame, page);
		
		//em.persist(this);
		
		System.out.println("OKAY, HERE'S THE NEW MAP:\n" + pageMap);
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
	
	// HTML / Template Methods
	
	public String getFrameTableTitle() {
		return "Physical Memory (" + MEMORY_MAX_SIZE + " bytes)";
	}
	
	public String getActionDescription() {
		return currentActionDescription;
	}
	
	public List<String> getFrameTable() {
		List<String> framesHTML = new ArrayList<String>(frames);
		
		for(Map.Entry<Frame, Page> slot : pageMap.entrySet()) {
			Page p = slot.getValue();
			
			framesHTML.add(p.toString());
		}
		
		return framesHTML;
	}
	
	public String getFrameTableHTML() {
		String table = "<table id=\"process_table\" align=\"center\">";
		
		for(Map.Entry<Frame, Page> slot : pageMap.entrySet())
			table += String.format("<tr><td class=\"pt_frame_label\">Frame %s</td><td class=\"pt_frame\">%s</td></tr>", slot.getKey(), slot.getValue());

		table += "</table>";
		table += "<input type=\"button\" value=\"Next State >\" />";
		
		return table;
	}
}
