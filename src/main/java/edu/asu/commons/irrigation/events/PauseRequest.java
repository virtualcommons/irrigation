package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Client request to pause flow.  Not always allowed.  
 * 
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class PauseRequest extends AbstractPersistableEvent {

    private static final long serialVersionUID = 5080329943962617402L;

    public PauseRequest(Identifier id) {
		super(id);
	}
    
    /**
     * Copy constructor - used to reconstruct on the server-side and guarantee uniqueness / sort-order.
     * 
     * @param pauseRequest
     */
    public PauseRequest(PauseRequest pauseRequest) {
        super(pauseRequest.id);
    }
	
}
