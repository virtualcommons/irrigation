package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    
    public ChatPanel() {
        initGuiComponents();
    }
    
    public ChatPanel(IrrigationClient irrigationClient) {
        this();
        setIrrigationClient(irrigationClient);
    }

    private class TextEntryPanel extends JPanel {
        private JLabel timeLeftLabel;
        private JTextField chatField;
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
            timeLeftPanel.setLayout(new BoxLayout(timeLeftPanel, BoxLayout.LINE_AXIS));
            timeLeftLabel = new JLabel("40");
            timeLeftLabel.setFont(new Font("Arial", Font.BOLD, 14));
            timeLeftLabel.setForeground(new Color(0x0000dd));
            timeLeftPanel.add(new JLabel(" Time left: "));
            timeLeftPanel.add(timeLeftLabel);

            add(timeLeftPanel, BorderLayout.PAGE_START);
            add(chatField, BorderLayout.CENTER);
            add(sendButton, BorderLayout.PAGE_END);
        }

        private void sendMessage() {
            String message = chatField.getText();
            if (message != null && ! message.isEmpty() && targetIdentifier != null) {
            	chatField.setText("");
            	irrigationClient.transmit(new ChatRequest(getClientId(), message, targetIdentifier));
            	displayMessage(
            			String.format("%s -> %s", getChatHandle(getClientId()), getChatHandle(targetIdentifier)), 
            			message);
            }
            chatField.requestFocusInWindow();
        }

        private void setTimeRemaining(long timeRemaining) {
            timeLeftLabel.setText(String.format(" %d s", timeRemaining / 1000L));
        }

    }

    private final static String HANDLE_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static String CHAT_INSTRUCTIONS = "<h3>Chat Instructions</h3><p>You now have the opportunity to chat for 40 seconds." +
    "  You can discuss whatever you want related to the experiment with some restrictions.  You may not promise the other participant(s) side " +
    "payments or threaten them with any consequence (e.g., physical violence) after the experiment is finished.  Also, you may not reveal your real identity." +
    "We are monitoring chat traffic - if we notice a violation of the rules we will remove the group from the room until the other groups are finished with the experiment.</p>" +
    "<p>You can send messages by typing in the text field at the bottom of the screen and then hit return or click send.  " +
    "The time left for the discussion is displayed above the text field at the bottom of the screen.</p>";

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

    public void setTimeRemaining(long timeRemaining) {
        textEntryPanel.setTimeRemaining(timeRemaining);
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
        chatInstructionsPane.setText(CHAT_INSTRUCTIONS);
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
            throw new RuntimeException(e);
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
        client.getEventChannel().add(this, new EventTypeProcessor<ChatEvent>(ChatEvent.class) {
            public void handle(final ChatEvent chatEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        displayMessage(getChatHandle(chatEvent.getSource()) + " -> "
                                + getChatHandle(chatEvent.getTarget()), chatEvent.toString());
                    }
                });
            }

        });
    }

}
