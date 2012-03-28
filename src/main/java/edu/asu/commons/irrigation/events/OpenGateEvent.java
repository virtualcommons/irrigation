package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Signifies that a client opened a gate.
 * 
 * @author Allen Lee
 * @revision $Rev$
 */
public class OpenGateEvent extends AbstractPersistableEvent {

    private static final long serialVersionUID = -4902131375099741851L;

    public OpenGateEvent(Identifier id) {
        super(id);
    }

    public String toString() {
        return id + " opened gate";
    }

}
