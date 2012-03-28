package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ShowRequest;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Facilitator request to display the quiz.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ShowQuizRequest extends AbstractEvent implements ShowRequest<ShowQuizRequest> {

    private static final long serialVersionUID = 383560843031641044L;

    public ShowQuizRequest(Identifier id) {
        super(id);
    }

    @Override
    public ShowQuizRequest clone(Identifier id) {
        return new ShowQuizRequest(id);
    }

}
