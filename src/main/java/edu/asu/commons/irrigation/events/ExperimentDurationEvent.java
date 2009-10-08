package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class ExperimentDurationEvent extends AbstractEvent implements ExperimentUpdateEvent {

    private static final long serialVersionUID = 5074385986630790547L;
    
    private final long time;
    
	public ExperimentDurationEvent(Identifier id,long time) {
		super(id);
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
	


}
