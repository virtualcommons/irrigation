package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
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

import edu.asu.commons.event.ChatEvent;
import edu.asu.commons.event.ChatRequest;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.net.Identifier;
import edu.asu.commons.ui.UserInterfaceUtils;

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

    private JScrollPane messageScrollPane;

    private JTextPane messageWindow;

    private TextEntryPanel textEntryPanel;

    private JEditorPane chatInstructionsPane;
    
    private JTextField chatField;

    public ChatPanel(IrrigationClient irrigationClient) {
        this.irrigationClient = irrigationClient;
        initGuiComponents();
        chatInstructionsPane.setText(irrigationClient.getServerConfiguration().getChatInstructions());
        irrigationClient.getEventChannel().add(this, new EventTypeProcessor<ChatEvent>(ChatEvent.class) {
            public void handle(final ChatEvent chatEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        displayMessage(getChatHandle(chatEvent.getSource()),
                                chatEvent.toString());
                    }
                });
            }

        });
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
//            final JButton sendButton = new JButton("Send");
//            sendButton.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent event) {
//                    sendMessage();
//                }
//            });
            JPanel timeLeftPanel = new JPanel();
            int chatDuration = irrigationClient.getServerConfiguration().getChatDuration();
            timeLeftProgressBar = new JProgressBar(0, chatDuration);
            timeLeftProgressBar.setStringPainted(true);
            timeLeftPanel.setLayout(new BorderLayout());
            timeLeftPanel.add(timeLeftProgressBar, BorderLayout.CENTER);

            add(timeLeftPanel, BorderLayout.PAGE_START);
            add(chatField, BorderLayout.CENTER);
//            add(sendButton, BorderLayout.PAGE_END);
        }

        private void sendMessage() {
            String message = chatField.getText();
            if (message != null && ! message.isEmpty() && targetIdentifier != null) {
                displayMessage(getChatHandle(getClientId()), message);
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

    private void addStylesToMessageWindow() {
        StyledDocument styledDocument = messageWindow.getStyledDocument();
        // and why not have something like... StyleContext.getDefaultStyle() to
        // replace this junk
        Style defaultStyle = getDefaultStyle();
        StyleConstants.setFontSize(defaultStyle, 16);
        StyleConstants.setFontFamily(defaultStyle, UserInterfaceUtils.getDefaultFont().getFamily());
        StyleConstants.setBold(styledDocument.addStyle("bold", defaultStyle), true);
        StyleConstants.setItalic(styledDocument.addStyle("italic", defaultStyle), true);
        StyleConstants.setUnderline(styledDocument.addStyle("underline", defaultStyle), true);

    }

    private Style getDefaultStyle() {
        return StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
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
        messageWindow.setBackground(Color.WHITE);
        messageScrollPane = new JScrollPane(messageWindow);
//        UserInterfaceUtils.addStyles(messageWindow, 16);
        addStylesToMessageWindow();
        textEntryPanel = new TextEntryPanel();
        chatInstructionsPane = UserInterfaceUtils.createInstructionsEditorPane();
        JScrollPane chatInstructionsScrollPane = new JScrollPane(chatInstructionsPane);
        add(chatInstructionsScrollPane, BorderLayout.PAGE_START);
        add(messageScrollPane, BorderLayout.CENTER);
//        add(participantButtonPanel, BorderLayout.EAST);
        add(textEntryPanel, BorderLayout.PAGE_END);
    }

    public void displayMessage(String chatHandle, String message) {
        final StyledDocument document = messageWindow.getStyledDocument();
        if (!chatHandle.endsWith(":")) {
            chatHandle = chatHandle.concat(": ");
        }
        try {
            document.insertString(0, chatHandle, document.getStyle("bold"));
            document.insertString(chatHandle.length(), message + "\n", getDefaultStyle());
            messageWindow.setCaretPosition(0);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void initialize(List<Identifier> participants) {
        displayMessage("System Message", " ---- chat round starting ---- ");
        if (HANDLES != null) {
            return;
        }
        HANDLES = new String[participants.size()];
        // FIXME: shuffle handles?
        for (int i = HANDLES.length; --i >= 0;) {
            HANDLES[i] = String.valueOf(HANDLE_STRING.charAt(i));
            chatHandles.put(participants.get(i), HANDLES[i]);
        }
    }
    
    public Identifier getClientId() {
    	return irrigationClient.getId();
    }

	public void setFocusInChatField() {
		chatField.requestFocusInWindow();
	}

}
