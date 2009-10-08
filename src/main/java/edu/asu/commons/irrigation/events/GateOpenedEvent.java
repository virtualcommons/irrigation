package edu.asu.commons.irrigation.events;

import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class GateOpenedEvent extends TransferFileInformationEvent {
    
    private static final long serialVersionUID = -4902131375099741851L;

	public GateOpenedEvent(Identifier id) {
		super(id);
	}

}
