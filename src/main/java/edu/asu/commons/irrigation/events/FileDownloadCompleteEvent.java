package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class FileDownloadCompleteEvent extends AbstractEvent {

    private static final long serialVersionUID = 2566276891605754835L;

    public FileDownloadCompleteEvent(Identifier id) {
		super(id);
	}



}
