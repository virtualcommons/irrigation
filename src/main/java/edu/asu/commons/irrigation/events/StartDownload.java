package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class StartDownload extends AbstractEvent {

    private static final long serialVersionUID = 2790726104971226910L;
    private int fileSelected;
	
	public StartDownload(Identifier id) {
		super(id);
	}
	
	public void setFileSelected(int fileSelected){
		this.fileSelected = fileSelected;
	}
	
	public int getFileSelected() {
		return fileSelected;
	}



}
