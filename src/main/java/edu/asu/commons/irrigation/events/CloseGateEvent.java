package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class CloseGateEvent extends AbstractPersistableEvent {

    private static final long serialVersionUID = -6932559955912875464L;

    public CloseGateEvent(Identifier id) {
		super(id);
	}

}
