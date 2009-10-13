package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Request from the facilitator to display instructions on the client side.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ShowInstructionsRequest extends AbstractEvent implements FacilitatorRequest {

	private static final long serialVersionUID = 798700489117350818L;
	
	public ShowInstructionsRequest(Identifier id){
		super(id);
	}

}
