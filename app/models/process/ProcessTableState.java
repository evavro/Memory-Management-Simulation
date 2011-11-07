package models.process;

import java.util.ArrayList;

public class ProcessTableState {

	ArrayList<ProcessPageTable> tables = new ArrayList<ProcessPageTable>();
	int currentState;
	
	public void addProcessTable(final ProcessPageTable table) {
		tables.add(table);
	}
	
	public int getCurrentState() {
		return currentState;
	}
}
