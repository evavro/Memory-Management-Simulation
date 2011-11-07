package controllers;

import java.io.File;
import java.util.List;

import play.libs.IO;
import play.mvc.Controller;

public class ProcessController extends Controller {
	
	// http://stackoverflow.com/questions/4271260/simple-ajax-with-play
	
	// KEEP TRACK OF ALL FRAMES!
	
	public static void uploadFile(final String title, final File file) throws Exception {
		if(file == null)
			throw new Exception("No file selected for upload");
		
		final List<String> lines = IO.readLines(file);
		//final ProcessTable processes = new ProcessTable();
		
		try {
			for(String line : lines) {
				System.out.println("Line: " + line);
				
				// True = event, false = exit
				if(determineEvent(line)) {
					int[] intChunks = splitDataToInts(line);
					
					//processes.addProcess(new EnterProcess(intChunks[0], intChunks[1], intChunks[2]));
				} else {
					// Exit process
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		renderTemplate("Application/ProcessTable.html");
		
		System.out.println(String.format("Uploaded and processed %s", file.getName()));
	}
	
	// True = Enter, False = Exit
	private static boolean determineEvent(final String line) throws Exception {
		final String[] chunks = splitData(line);
		final int chunkSize = chunks.length;
		
		if(chunkSize == 3)
			return true;
		else if(chunkSize == 2)
			return false;
		else
			throw new Exception("File format error");
	}
	
	private static String[] splitData(final String line) {
		return line.split(" ");
	}
	
	private static int[] splitDataToInts(final String line) {
		String[] chunks = splitData(line);
		int[] intChunks = new int[chunks.length];
		
		for(int i = 0; i < chunks.length; i++)
			intChunks[i] = Integer.parseInt(chunks[i]);
		
		return intChunks;
	}
	
	// add an exception @Catch glove to put all the errors into the proper html format
}