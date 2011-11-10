package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Transient;

import models.process.EmptyPage;
import models.process.Frame;
import models.process.Page;
import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
public class MemoryState extends Model {

	// What pages belong in what frames at a certain state in memory
	/*@ElementCollection
    @MapKeyColumn(name="frameId")
    @Column(name="pageId", nullable=true) // was Map<Frame, Page>*/
	
	@ElementCollection
    @MapKeyColumn(name="frameId")
    @Column(name="pageId")
	@OneToMany(cascade = CascadeType.ALL)
	public Map<Frame, Page> pageMap = new HashMap<Frame, Page>();
	
	@Column(nullable=true)
	public MemoryState priorState;
	
	// Create a brand new memory state with all open frames
	public MemoryState() {
		/*final List<Frame> frames = Frame.findAll();
		
		for(Frame f : frames)
			pageMap.put(f, new EmptyPage());
				*/
		//save();
	}
	
	
	// Create a new memory state based on the previous memory state
	public MemoryState(final MemoryState priorState) throws Exception {
		this(priorState.pageMap);
	}
	
	public MemoryState(final Map<Frame, Page> pageMap) throws Exception {
		if(pageMap.size() != Frame.count())
			throw new Exception("Cannot create a partial memory map, you must supply data for all frames in memory. Only provided " + pageMap.size() + " out of " + Frame.count() + " frames");
		
		// if HashMap size != Frames.count(), then all pages haven't been accounted for!
		this.pageMap = new HashMap(pageMap);
	}
	
	/*public void setPageMap(final Map<Frame, Page> pageMap) {
		this.pageMap = pageMap;
		
		//save();
	}*/
	
	// Places a page into memory using the worst-fit algorithm
	public void allocatePage(final Page page) {
		final Frame largestFrame = getLargestFrame();
		final int freeMemInFrame = getFreeBytesInFrame(largestFrame);
		
		System.out.println("Allocating page (" + page + ") @ largest frame: " + largestFrame + " out of " + Frame.count() + " frames");
		
		System.out.println("LARGEST FRAME IS " + largestFrame + ", size " + freeMemInFrame);
		
		if(page.getSize() < freeMemInFrame)
			pageMap.put(largestFrame, page);
		
		//em.persist(this);
		save();
	}
	
	// Find the largest frame in memory (worst-fit algorithm)
	public Frame getLargestFrame() {
		List<Frame> frames = Frame.all().fetch();
		Frame largestFrame = frames.get(0);
		
		//System.out.println("IN GET LARGEST FRAME, FRAMES #: " + frames.size());
		
		// Find the frame in memory with the most free memory
		// for(Map.Entry<Frame, Page> memSlot : pageMap.entrySet()) {
		for(Frame frame : frames) {
			//Frame currFrame = memSlot.getKey();
			int currFrameFreeMem = getFreeBytesInFrame(frame);

			// If memory is empty, the first frame is the biggest (they're all equally sized)
			if(isMemoryEmpty()) {
				largestFrame = frame;
			} else {
				//System.out.println("IN GET LARGEST FRAME Frame #" + frame + ", free bytes in frame: " + getFreeBytesInFrame(frame));
				
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
	
	// getPhysicalMemoryHTML
	public String getPhysicalMemoryHTML() {
		
		return "";
	}
}
