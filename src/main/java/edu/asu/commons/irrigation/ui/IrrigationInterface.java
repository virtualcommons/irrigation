package edu.asu.commons.irrigation.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;

import edu.asu.commons.util.HtmlEditorPane;

/**
 * $Id$
 * 
 * static utility class for common UI methods to set up a consistent look & feel.
 * 
 * FIXME: push up to csidex
 * 
 * 
 * @author <a href='mailto:allen.lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public final class IrrigationInterface {
    
    public static final Font DEFAULT_PLAIN_FONT = new Font(getDefaultFont().getFamily(), Font.PLAIN, 16);
    public static final Font DEFAULT_BOLD_FONT = new Font(getDefaultFont().getFamily(), Font.BOLD, 16);
    

    public static Font getDefaultFont() {
        return UIManager.getFont("Label.font");
    }
    
    public static void addStyles(JEditorPane editorPane, int fontSize) {
        editorPane.setContentType("text/html");
        Font font = getDefaultFont();
        String bodyRule = String.format("body { font-family: %s; font-size: %s pt; }", font.getFamily(), fontSize);
        ((HTMLDocument) editorPane.getDocument()).getStyleSheet().addRule(bodyRule); 
    }
    
    public static HtmlEditorPane createInstructionsEditorPane() {
        return createInstructionsEditorPane(false);
    }
    
    public static HtmlEditorPane createInstructionsEditorPane(boolean editable) {
        final HtmlEditorPane htmlPane = new HtmlEditorPane();
        htmlPane.setEditable(editable);
        htmlPane.setDoubleBuffered(true);
        htmlPane.setBackground(Color.WHITE);
        IrrigationInterface.addStyles(htmlPane, 16);
        return htmlPane;
    }
}
