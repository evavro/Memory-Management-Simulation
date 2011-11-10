package models.process;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.MemoryManager;
import models.MemoryState;
import play.db.jpa.Model;

//This represents what would be commonly known as a PCB (Process Control Block) in an operating system's kernel
@Entity
public class Process extends Model {
	
	// Process ID provided by the source file
	public int procId;
	
	// Size of text segment (in bytes) provided by the source file
	public int textSize;
	
	// Size of data segment (in bytes) provided by the source file
	public int dataSize;
	
	// The RAM that this process is contained in (the entire physical object)
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public MemoryManager memory;
	
	/*@OneToOne(fetch = FetchType.LAZY, 
			   cascade = { CascadeType.PERSIST,
            			   CascadeType.MERGE,
            			   CascadeType.REMOVE })
	public ProcessPageTable table; 	// a.k.a. ProcessPageTable*/
	
	// These may not be necessary...
	@OneToMany(fetch = FetchType.LAZY, 
			   cascade = { CascadeType.PERSIST,
            			   //CascadeType.MERGE,
            			   CascadeType.REMOVE })
	public List<TextPage> textPages;
	
	// These may not be necessary...
	@OneToMany(fetch = FetchType.LAZY, 
			   cascade = { CascadeType.PERSIST,
            			   //CascadeType.MERGE,
            			   CascadeType.REMOVE })
	public List<DataPage> dataPages;
	
	public Process(final int procId,
				   final int textSize,
				   final int dataSize,
				   final MemoryManager memory) throws Exception {
		
		this.procId = procId;
		this.textSize = textSize;
		this.dataSize = dataSize;
		this.memory = memory;
		
		//System.out.println("Process " + procId + ": - textSize: " + textSize + ", dataSize: " + dataSize);
		
		// Save this process into JPA so that we can reference it in the ProcessPageTable
		save();
		
		// Create the page table for this process
		//this.table = new ProcessPageTable(this);
		
		// Save this process again with the newly created process page table
		//save();
		
		createPages();
	}
	
	// Creates the page data segments that will be switched in and out by the memory manager (no actual allocation occurs here!)
	private void createPages() throws Exception {
		System.out.println(String.format("Process %s: Number text pages: %s, Number data pages: %s", procId, determineNumPages(textSize), determineNumPages(dataSize)));
		
		createDataPages();
		createTextPages();
		
		save();
	}
	
	public void createDataPages() throws Exception {
		final int maxPageSize = Page.PAGE_MAX_SIZE;
		final int numData = determineNumPages(dataSize);
		
		int pageSize;
		int remainingMemToAlloc = dataSize;
		
		for(int i = 0; i < numData; i++) {
			if(remainingMemToAlloc - maxPageSize > 0) {
				pageSize = maxPageSize;
			} else {
				pageSize = remainingMemToAlloc;	
			}
			
			remainingMemToAlloc -= pageSize;
			
			System.out.println("Create data page of size " + pageSize +", remaining memory: " + remainingMemToAlloc);
			
			dataPages.add(new DataPage(i, pageSize, this));
		}
	}
	
	public void createTextPages() throws Exception {
		final int maxPageSize = Page.PAGE_MAX_SIZE;
		final int numText = determineNumPages(textSize);
		
		int pageSize;
		int remainingMemToAlloc = textSize;
		
		for(int i = 0; i < numText; i++) {
			if(remainingMemToAlloc - maxPageSize > 0) {
				pageSize = maxPageSize;
			} else {
				pageSize = remainingMemToAlloc;	
			}
			
			remainingMemToAlloc -= pageSize;
			
			System.out.println("Create text page of size " + pageSize +", remaining memory: " + remainingMemToAlloc);
			
			textPages.add(new TextPage(i, pageSize, this));
		}
		
	}
	
	public int determineNumTextPages() {
		return determineNumPages(textSize);
	}
	
	public int determineNumDataPages() {
		return determineNumPages(dataSize);
	}

	public int determineNumPages(final int size) {
		final int pageSize = Page.PAGE_MAX_SIZE;
		final int roughDivide = size / pageSize;
		final int mod = size % pageSize;
		
		return mod == 0 ? roughDivide : roughDivide + 1;
	}
	
	public void updateProcessState() {
		// save table
		// 		save states in table
		// save this
	}
	
	public List<Page> getPages() {
		return Page.find("process=?", this).fetch();
	}
	
	public List<DataPage> getDataPages() {
		return DataPage.find("process=?", this).fetch();
	}
	
	public List<TextPage> getTextPages() {
		return TextPage.find("process=?", this).fetch();
	}
	
	public void terminate() {
		// delete all related pages
		// delete ProcessPageTables
		// delete ProcessPageTableState
		
		delete();
	}
	
	@Override
	public String toString() {
		return "Process " + procId;
	}
}