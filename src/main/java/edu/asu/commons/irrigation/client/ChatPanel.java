package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.asu.commons.event.ChatEvent;
import edu.asu.commons.event.ChatRequest;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Chat panel used to communicate with other players.  
 * 
 * FIXME: randomize mappings from handle (e.g., A -> 1, B -> 2, C -> 3 ...) so that it's
 * not linear.
 * 
 * @author alllee
 * @version $Revision$
 */

@SuppressWarnings("serial")
public class ChatPanel extends JPanel {

    private IrrigationClient irrigationClient;

    private Identifier clientId;

    private JScrollPane messageScrollPane;

    private JTextPane messageWindow;

    private TextEntryPanel textEntryPanel;

    private JEditorPane chatInstructionsPane;
    
    private JTextField chatField;

    
    public ChatPanel() {
        initGuiComponents();
    }
    
    public ChatPanel(IrrigationClient irrigationClient) {
        this();
        setIrrigationClient(irrigationClient);
    }

    private class TextEntryPanel extends JPanel {
        private JProgressBar timeLeftProgressBar;
        private Identifier targetIdentifier = Identifier.ALL;

        public TextEntryPanel() {
            super();
            setLayout(new BorderLayout(3, 3));
            chatField = new JTextField();
            chatField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendMessage();
                    }
                }
            });
            final JButton sendButton = new JButton("Send");
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    sendMessage();
                }
            });
            JPanel timeLeftPanel = new JPanel();
            timeLeftProgressBar = new JProgressBar(0, 60);
            timeLeftProgressBar.setStringPainted(true);
            timeLeftPanel.setLayout(new BorderLayout());
            timeLeftPanel.add(timeLeftProgressBar, BorderLayout.CENTER);

            add(timeLeftPanel, BorderLayout.PAGE_START);
            add(chatField, BorderLayout.CENTER);
            add(sendButton, BorderLayout.PAGE_END);
        }

        private void sendMessage() {
            String message = chatField.getText();
            if (message != null && ! message.isEmpty() && targetIdentifier != null) {
                displayMessage(String.format("%s -> %s", getChatHandle(getClientId()), getChatHandle(targetIdentifier)), 
                        message);
            	chatField.setText("");
            	irrigationClient.transmit(new ChatRequest(getClientId(), message, targetIdentifier));
 
            }
            chatField.requestFocusInWindow();
        }

        private void setTimeLeft(long timeLeft) {
            int timeLeftInSeconds = (int) (timeLeft / 1000L);
            timeLeftProgressBar.setValue(timeLeftInSeconds);
            timeLeftProgressBar.setString(String.format("%d sec", timeLeftInSeconds));
        }

    }

    private final static String HANDLE_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static String[] HANDLES;
    
    private Map<Identifier, String> chatHandles = new HashMap<Identifier, String>();

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ChatPanel chatPanel = new ChatPanel();
        Identifier selfId = new Identifier(){};
        chatPanel.clientId = selfId;
        chatPanel.initialize(Arrays.asList(new Identifier[] {
                new Identifier(){}, new Identifier(){}, 
                new Identifier(){}, selfId }));
        frame.add(chatPanel);
        frame.setSize(new Dimension(400, 400));
        frame.setVisible(true);
    }


    private void addStylesToMessageWindow() {
        StyledDocument styledDocument = messageWindow.getStyledDocument();
        // and why not have something like... StyleContext.getDefaultStyle() to
        // replace this junk
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        // Style regularStyle = styledDocument.addStyle("regular",
        // defaultStyle);
        StyleConstants.setFontFamily(defaultStyle, "Helvetica");
        StyleConstants.setBold(styledDocument.addStyle("bold", defaultStyle),
                true);
        StyleConstants.setItalic(styledDocument
                .addStyle("italic", defaultStyle), true);
    }

    public void setTimeLeft(long timeLeft) {
        textEntryPanel.setTimeLeft(timeLeft);
    }

    private String getChatHandle(Identifier identifier) {
        if (identifier.equals(Identifier.ALL)) {
            return " all ";
        }
        return chatHandles.get(identifier);
    }

    private void initGuiComponents() {
        setLayout(new BorderLayout(4, 4));
        messageWindow = new JTextPane();
        messageWindow.setEditable(false);
        messageScrollPane = new JScrollPane(messageWindow);
        addStylesToMessageWindow();
        textEntryPanel = new TextEntryPanel();
        chatInstructionsPane = new JEditorPane();
        chatInstructionsPane.setContentType("text/html");
        chatInstructionsPane.setEditorKit(new HTMLEditorKit());
        chatInstructionsPane.setEditable(false);
        JScrollPane chatInstructionsScrollPane = new JScrollPane(chatInstructionsPane);
        add(chatInstructionsScrollPane, BorderLayout.PAGE_START);
        add(messageScrollPane, BorderLayout.CENTER);
//        add(participantButtonPanel, BorderLayout.EAST);
        add(textEntryPanel, BorderLayout.PAGE_END);
    }

    private void displayMessage(String chatHandle, String message) {
        //		String chatHandle = getChatHandle(source);
        StyledDocument document = messageWindow.getStyledDocument();
        try {
            document.insertString(document.getLength(), chatHandle + " : ",
                    document.getStyle("bold"));
            document.insertString(document.getLength(), message + "\n", null);
            messageWindow.setCaretPosition(document.getLength());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void initialize(List<Identifier> participants) {
        System.err.println("Setting participants: " + participants);
        if (HANDLES != null) {
            return;
        }
        HANDLES = new String[participants.size()];
        // FIXME: shuffle handles?
        for (int i = HANDLES.length; --i >= 0;) {
            HANDLES[i] = " " + HANDLE_STRING.charAt(i) + " ";
            chatHandles.put(participants.get(i), HANDLES[i]);
        }
    }
    
    public Identifier getClientId() {
    	if (clientId == null) {
    		clientId = irrigationClient.getId();
    	}
    	return clientId;
    }

    public void setIrrigationClient(IrrigationClient client) {
        this.irrigationClient = client;
        chatInstructionsPane.setText(irrigationClient.getServerConfiguration().getChatInstructions());
        client.getEventChannel().add(this, new EventTypeProcessor<ChatEvent>(ChatEvent.class) {
            public void handle(final ChatEvent chatEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        displayMessage(String.format("%s -> %s", getChatHandle(chatEvent.getSource()), getChatHandle(chatEvent.getTarget())),
                                chatEvent.toString());
                    }
                });
            }

        });
    }

	public void setFocusInChatField() {
		chatField.requestFocusInWindow();
	}

}
