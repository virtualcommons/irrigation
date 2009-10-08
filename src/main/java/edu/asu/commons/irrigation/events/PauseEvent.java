package edu.asu.commons.irrigation.events;

import edu.asu.commons.net.Identifier;



/**
 * @author Sanket
 *
 */
public class PauseEvent extends TransferFileInformationEvent {

    private static final long serialVersionUID = -6518246719469415026L;
    
	public PauseEvent(Identifier id) {
		super(id);
	}



}
