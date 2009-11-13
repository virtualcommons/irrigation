package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Facilitator request to display the quiz.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ShowQuizRequest extends AbstractEvent implements FacilitatorRequest {

    private static final long serialVersionUID = 383560843031641044L;

    public ShowQuizRequest(Identifier id) {
        super(id);
    }
    
}
