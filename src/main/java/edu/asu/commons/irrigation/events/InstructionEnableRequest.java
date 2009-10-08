/**
 * 
 */
package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class InstructionEnableRequest extends AbstractEvent implements FacilitatorRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 798700489117350818L;
	
	public InstructionEnableRequest(Identifier id){
		super(id);
	}

}
