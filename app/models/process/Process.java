package models.process;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.MemoryManager;
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
	
	// Associated text page memory segments
	@OneToMany(fetch = FetchType.LAZY, 
			   cascade = { CascadeType.PERSIST,
            			   CascadeType.REMOVE })
	public List<TextPage> textPages;
	
	// Associated data page memory segments
	@OneToMany(fetch = FetchType.LAZY, 
			   cascade = { CascadeType.PERSIST,
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
		
		// Save this process into JPA so that we can reference it in the ProcessPageTable
		save();
		
		createPages();
	}
	
	// Creates the page data segments that will be switched in and out by the memory manager (no actual allocation occurs here!)
	private void createPages() throws Exception {
		System.out.println(String.format("Process %s: Number text pages: %s, Number data pages: %s", procId, determineNumPages(textSize), determineNumPages(dataSize)));
		
		createTextPages();
		createDataPages();
		
		save();
	}
	
	
	// Create the text page memory segments, but do not allocate them yet!
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
			
			textPages.add(new TextPage(i, pageSize, this));
		}
	}
	
	// Create the data page memory segments, but do not allocate them yet!
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
			
			dataPages.add(new DataPage(i, pageSize, this));
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

	public List<Page> getPages() {
		return Page.find("process=?", this).fetch();
	}
	
	public List<DataPage> getDataPages() {
		return DataPage.find("process=?", this).fetch();
	}
	
	public List<TextPage> getTextPages() {
		return TextPage.find("process=?", this).fetch();
	}
	
	// Unused for now...
	public void terminate() {
		delete();
	}
	
	@Override
	public String toString() {
		return "Process " + procId;
	}
}