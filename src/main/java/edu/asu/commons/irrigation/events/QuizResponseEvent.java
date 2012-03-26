package edu.asu.commons.irrigation.events;

import java.util.List;
import java.util.Properties;

import edu.asu.commons.event.AbstractPersistableEvent;
import edu.asu.commons.event.ClientRequest;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * A client's quiz responses for a given quiz page.
 * 
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class QuizResponseEvent extends AbstractPersistableEvent implements ClientRequest {

    private static final long serialVersionUID = -7081410122722056083L;


    private Properties responses;

    private List<String> incorrectAnswers;

    public QuizResponseEvent(Identifier id, Properties responses, List<String> incorrectAnswers) {
        super(id);
        this.responses = responses;
        this.incorrectAnswers = incorrectAnswers;
    }

    public Properties getResponses() {
        return responses;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public String toString() {
        return id + " quiz responses: " + responses + "\n\t incorrect: " + incorrectAnswers;
    }

    public int getNumberOfCorrectQuizAnswers() {
        return responses.size() - 1 - getIncorrectAnswers().size();
    }
}
