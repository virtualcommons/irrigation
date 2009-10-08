package edu.asu.commons.irrigation.events;

import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class StopDownloadEvent extends TransferFileInformationEvent {

    private static final long serialVersionUID = -6932559955912875464L;

    public StopDownloadEvent(Identifier id) {
		super(id);
	}

}
