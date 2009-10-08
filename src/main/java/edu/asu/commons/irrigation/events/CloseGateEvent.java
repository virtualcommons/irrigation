package edu.asu.commons.irrigation.events;

import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class CloseGateEvent extends TransferFileInformationEvent {

    private static final long serialVersionUID = -6932559955912875464L;

    public CloseGateEvent(Identifier id) {
		super(id);
	}

}
