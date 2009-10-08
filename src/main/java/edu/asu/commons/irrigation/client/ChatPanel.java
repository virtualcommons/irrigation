package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
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
import edu.asu.commons.event.EventChannel;
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
 * @version $Revision: $
 */

@SuppressWarnings("serial")
public class ChatPanel extends JPanel {

    private IrrigationClient client;

    private JTextField textField;

    public JTextField getJTextField(){
        if (textField == null) {
            textField = new JTextField();
        }
        return textField;
    }

    private class TextEntryPanel extends JPanel {
        private JLabel timeLeftLabel;

        private Identifier targetIdentifier = Identifier.ALL;

        public TextEntryPanel() {
            super();
            setLayout(new BorderLayout(3, 3));

            textField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    // System.err.println("event keycode is: " +
                    // event.getKeyCode());
                    // System.err.println("vk_enter: " + KeyEvent.VK_ENTER);
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendMessage(textField);
                    }
                }
            });
            final JButton sendButton = new JButton("Send");
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    sendMessage(textField);
                }
            });
            JPanel timeLeftPanel = new JPanel();
            timeLeftPanel.setLayout(new BoxLayout(timeLeftPanel,
                    BoxLayout.LINE_AXIS));
            timeLeftLabel = new JLabel(" 50");
            timeLeftLabel.setFont(new Font("Arial", Font.BOLD, 14));
            timeLeftLabel.setForeground(new Color(0x0000dd));
            timeLeftPanel.add(new JLabel(" Time left: "));
            timeLeftPanel.add(timeLeftLabel);

            add(timeLeftPanel, BorderLayout.NORTH);
            add(textField, BorderLayout.CENTER);
            add(sendButton, BorderLayout.SOUTH);
        }

        private void sendMessage(JTextField textField) {
            String message = textField.getText();
            // System.err.println("message: " + message);
            if (message == null || "".equals(message)) {
                return;
            }
            if (targetIdentifier == null) {
                return;
            }
            textField.setText("");
            client.transmit(new ChatRequest(clientId, message, targetIdentifier));
            System.err.println("Sending a new chat request");
            displayMessage(getChatHandle(clientId) + " -> "
                    + getChatHandle(targetIdentifier), message);
            textField.requestFocusInWindow();
        }

        private void setTimeRemaining(long timeRemaining) {
            timeLeftLabel.setText(String.format(" %d s", timeRemaining / 1000L));
        }

        private void setTargetHandle(Identifier targetIdentifier) {
            //            this.targetIdentifier = targetIdentifier;
            //            if (targetIdentifier == Identifier.ALL) {
            //                timeLeftLabel.setText("everyone");
            //            } else {
            //                timeLeftLabel.setText(getChatHandle(targetIdentifier));
            //            }
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

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ChatPanel chatPanel = new ChatPanel();
        Identifier selfId = new Identifier(){};
        chatPanel.setClientId(selfId);
        chatPanel.initialize(Arrays.asList(new Identifier[] {
                new Identifier(){}, new Identifier(){}, 
                new Identifier(){}, selfId }));
        frame.add(chatPanel);
        frame.setSize(new Dimension(400, 400));
        frame.setVisible(true);
    }

    private long timeRemaining;

    private Identifier clientId;

    private JScrollPane messageScrollPane;

    private JTextPane messageWindow;

    private EventChannel channel;

    // used by the participant to select which participant to send a message to.
    private JPanel participantButtonPanel;

    private List<Identifier> participants;

    private TextEntryPanel textEntryPanel;

    private JEditorPane chatInstructionsPane;

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

    private String getChatHandle(Identifier source) {
        if (source.equals(Identifier.ALL)) {
            return " all ";
        } else {
            int index = participants.indexOf(source);
            if (source.equals(clientId)) {
                return HANDLES[index] + "(you)";
            }
            return "   " + HANDLES[index] + "   ";
        }

    }

    private void initGuiComponents() {
        setLayout(new BorderLayout(4, 4));
        messageWindow = new JTextPane();
        messageWindow.setEditable(false);
        messageScrollPane = new JScrollPane(messageWindow);
        addStylesToMessageWindow();

        // set up the participant panel
        participantButtonPanel = new JPanel();
        // participantButtonPanel.setLayout(new
        // BoxLayout(participantButtonPanel,
        // BoxLayout.PAGE_AXIS));
        participantButtonPanel.setLayout(new GridLayout(0, 1));
        participantButtonPanel.setBackground(Color.GRAY);
        // JLabel selfLabel = new JLabel(getChatHandle(clientId));
        // selfLabel.setForeground(Color.ORANGE);
        // selfLabel.setBackground(Color.ORANGE);
        // selfLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton selfButton = new JButton(getChatHandle(clientId));
        selfButton.setEnabled(false);
        selfButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        participantButtonPanel.add(selfButton);
        participantButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        for (int i = 0; i < HANDLES.length; i++) {
            final Identifier targetId = participants.get(i);
            if (targetId.equals(clientId)) {
                continue;
            }
            String handle = HANDLES[i];
            JButton button = new JButton(handle);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // change stuff in the messageEntryPanel
                    textEntryPanel.setTargetHandle(targetId);
                }
            });
            participantButtonPanel.add(button);
        }
        // special case to send a message to everyone
        JButton sendAllButton = new JButton(" all ");
        sendAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textEntryPanel.setTargetHandle(Identifier.ALL);
            }
        });
        participantButtonPanel.add(sendAllButton);

        textEntryPanel = new TextEntryPanel();
        // orient the components in true lazyman fashion.

        chatInstructionsPane = new JEditorPane();
        chatInstructionsPane.setContentType("text/html");
        chatInstructionsPane.setEditorKit(new HTMLEditorKit());
        chatInstructionsPane.setEditable(false);
        JScrollPane chatInstructionsScrollPane = new JScrollPane(chatInstructionsPane);
        chatInstructionsPane.setText(CHAT_INSTRUCTIONS);


        add(chatInstructionsScrollPane, BorderLayout.NORTH);
        add(messageScrollPane, BorderLayout.CENTER);
        add(participantButtonPanel, BorderLayout.EAST);
        add(textEntryPanel, BorderLayout.SOUTH);

    }

    public void clear() {
        participants.clear();
    }

    private void displayMessage(String chatHandle, String message) {
        //		String chatHandle = getChatHandle(source);
        StyledDocument document = messageWindow.getStyledDocument();
        try {
            document.insertString(document.getLength(), chatHandle + " : ",
                    document.getStyle("bold"));
            document.insertString(document.getLength(), message + "\n", null);
            messageWindow.setCaretPosition(document.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void initialize(List<Identifier> participants) {
        System.err.println("Setting participants: " + participants);
        if (HANDLES != null) {
            return;
        }
        this.participants = participants;
        HANDLES = new String[participants.size()];
        for (int i = HANDLES.length; --i >= 0;) {
            HANDLES[i] = " " + HANDLE_STRING.charAt(i) + " ";
        }
        //      Collections.shuffle(Arrays.asList(HANDLES));
        //      System.err.println("handles: " + HANDLES);

        channel.add(this, new EventTypeProcessor<ChatEvent>(ChatEvent.class) {
            public void handle(final ChatEvent chatEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        displayMessage(getChatHandle(chatEvent.getSource()) + " -> "
                                // FIXME: either "all" or "you".
                                + getChatHandle(chatEvent.getTarget()), chatEvent.toString());
                    }
                });
            }

        });
        initGuiComponents();
    }

    public void setClientId(Identifier clientId) {
        this.clientId = clientId;
    }

    public void setClient(IrrigationClient client) {
        setClientId(client.getId());
        this.client = client;
    }

}
