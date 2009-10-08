package edu.asu.commons.irrigation.events;


import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.irrigation.server.ServerDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class ServerGameStateEvent extends AbstractEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6430739901754414073L;
	
	private ServerDataModel serverGameState;
	
	private long timeLeft;
	
	public ServerGameStateEvent(Identifier id, ServerDataModel serverGameState, long timeLeft) {
        super(id);
        this.serverGameState = serverGameState;
        this.timeLeft = timeLeft;
    }

	public ServerDataModel getServerGameState(){
		return serverGameState;
	}
	
	public long getTimeLeft(){
		return timeLeft;
	}
}
