package models.pages;

public class Page {
	
	int frame;
	int pageId;
	
	// each segment begins numbering its pages with page 0
	// (segment = text or data)
	
	public void updateFrame(final int frame) {
		this.frame = frame;
	}
}
