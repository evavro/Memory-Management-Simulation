package models.process;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import models.MemoryManager;

import play.db.jpa.Model;

@Entity
// This represents what would be commonly known as a PCB (Process Control Block) in an operating system's kernel
public class Process extends Model {
	
	// Process ID provided by the source file
	public int procId;
	
	// Size of text segment (in bytes) provided by the source file
	public int textSize;
	
	// Size of data segment (in bytes) provided by the source file
	public int dataSize;
	
	// The RAM that this process is contained in (the entire physical object)
	@ManyToOne
	public MemoryManager memory;
	
	@OneToOne
	public ProcessPageTable table; 	// a.k.a. ProcessPageTable
	
	public Process(final int procId,
				   final int textSize,
				   final int dataSize,
				   final MemoryManager memory) throws Exception {
		
		this.procId = procId;
		this.textSize = textSize;
		this.dataSize = dataSize;
		this.memory = memory;
		
		System.out.println("Process " + procId + ": - textSize: " + textSize + ", dataSize: " + dataSize);
		
		// Save this process into JPA so that we can reference it in the ProcessPageTable
		save();
		
		// Create the page table for this process and save it to JPA
		this.table = new ProcessPageTable(this).save();
		
		// Save this process again with the newly created process page table
		save();
		
		createMemorySegments();
	}
	
	// Creates
	private void createMemorySegments() throws Exception {
		System.out.println(String.format("Process %s: Number text pages: %s, Number data pages: %s", procId, determineNumPages(textSize), determineNumPages(dataSize)));
		
		createDataPages();
		createTextPages();
	}
	
	public void createTextPages() throws Exception {
		final int numText = determineNumPages(textSize);
		final int freeMemory = memory.freeMemory;
		final int totalSizePages = freeMemory - (freeMemory - textSize);
		final int pageSize = textSize / numText;
		
		for(int i = 0; i < numText; i++)
			new TextPage(i, pageSize, this);
	}
	
	public void createDataPages() throws Exception {
		final int numData = determineNumPages(dataSize);
		final int freeMemory = memory.freeMemory;
		final int totalSizePages = freeMemory - (freeMemory - dataSize);
		final int pageSize = textSize / numData;
		
		for(int i = 0; i < numData; i++)
			new DataPage(i, pageSize, this);
		
		// DON'T MALLOC HERE!! THE CREATE DATA PAGE AND CREATE TEXT PAGE ONLY CREATES THE PAGES AND TEXTS THAT CAN POSSIBLY EXIST!
		// YOU THEN PULL THEM IN AND OUT OF MEMORY AS NEEDED! (HANDLED BY MEMORY MANAGER!)
		//memory.malloc(totalSizePages);
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
}