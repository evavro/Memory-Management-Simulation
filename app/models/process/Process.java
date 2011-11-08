package models.process;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import models.pages.DataPage;
import models.pages.Page;
import models.pages.ProcessPageTable;
import models.pages.TextPage;

import play.db.jpa.Model;

@Entity
public class Process extends Model {
	
	// Process ID provided by the source file
	public int procId;
	
	// Size of text segment (in bytes) provided by the source file
	public int textSize;
	
	// Size of data segment (in bytes) provided by the source file
	public int dataSize;
	
	@OneToOne
	public ProcessPageTable table; 	// a.k.a. ProcessPageTable
	
	// Page size in bytes
	@Transient
	final int PAGE_SIZE = 512;
	
	public Process(final int procId,
				   final int textSize,
				   final int dataSize) {
		
		this.procId = procId;
		this.textSize = textSize;
		this.dataSize = dataSize;
		this.table = new ProcessPageTable(this);
		
		allocateMemory(); // allocate memory space and map
	}
	
	public void setTextPages() {
		final int numText = determineNumPages(textSize);
		
		for(int i = 0; i < numText; i++)
			new TextPage(i).save();
	}
	
	public void setDataPages() {
		final int numData = determineNumPages(dataSize);
		
		for(int i = 0; i < numData; i++)
			new DataPage(i).save();
	}
	
	private void allocateMemory() {
		final int numText = determineNumPages(textSize);
		final int numData = determineNumPages(dataSize);
		
		System.out.println(String.format("Process %s: Number text pages: %s, Number data pages: %s", procId, numText, numData));
		
		setDataPages();
		setTextPages();
		
		System.out.println(String.format("Process %s - Text pages: %s, Data pages: %s", procId, TextPage.count(), DataPage.count()));
	}
	
	public int determineNumTextPages() {
		return determineNumPages(textSize);
	}
	
	public int determineNumDataPages() {
		return determineNumPages(dataSize);
	}

	public int determineNumPages(final int size) {
		final int roughDivide = size / PAGE_SIZE;
		final int mod = size % PAGE_SIZE;
		
		return mod == 0 ? roughDivide : roughDivide + 1;
	}
	
	public void terminate() {
		// delete all related pages
		// delete ProcessPageTables
		// delete ProcessPageTableState
		
		delete();
	}
}