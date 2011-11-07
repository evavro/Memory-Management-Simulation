package models.process;

import play.db.jpa.Model;

public abstract class Process extends Model {
	// Process Id
	public int procId;
	
	// Size of text segment (in bytes) provided by the source file
	public int textSize;
	
	// Size of data segment (in bytes) provided by the source file
	public int dataSize;
	
	//public ProcessTable table; 	// a.k.a. ProcessPageTable
	
	// Page size in bytes
	final int PAGE_SIZE = 512;
	
	public Process(final int procId,
				   final int textSize,
				   final int dataSize) {
		this.procId = procId;
		this.textSize = textSize;
		this.dataSize = dataSize;
		//this.table = new ProcessTable();
		
		allocateMemory(); // allocate memory space and map
	}
	
	private void allocateMemory() {
		final int numText = determineNumPages(textSize);
		final int numData = determineNumPages(dataSize);
		
		System.out.println(String.format("Number text pages: %s, Number data pages: %s", numText, numData));
	}
	
	public int determineNumTextPages() {
		return determineNumPages(textSize);
	}
	
	public int determineNumDataPages() {
		return determineNumPages(dataSize);
	}

	public int determineNumPages(final int size) {
		int roughDivide = size / PAGE_SIZE;
		int mod = size % PAGE_SIZE;
		
		return mod == 0 ? roughDivide : roughDivide + 1;
	}
}