package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;

// Primary model - keeps track of all of all of the actions provided by a Process File
@Entity
public class MemoryManager extends Model {
	
	List<MemoryState> memStates = new ArrayList<MemoryState>();
}
