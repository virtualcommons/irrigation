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
	
	private int quizPage;
	
	private Properties responses;
	
	private List<String> incorrectAnswers;
	
	public QuizResponseEvent(Identifier id, int quizPage, Properties responses, List<String> incorrectAnswers) {
		super(id);
		this.quizPage = quizPage;
		this.responses = responses;
		this.incorrectAnswers = incorrectAnswers;
	}
	
	public int getQuizPage(){
		return quizPage;
	}
	
	public Properties getResponses() {
	    return responses;
	}
	
	public List<String> getIncorrectAnswers() {
	    return incorrectAnswers;
	}
}
