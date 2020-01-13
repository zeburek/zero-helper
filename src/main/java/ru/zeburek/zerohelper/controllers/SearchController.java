package ru.zeburek.zerohelper.controllers;

import javafx.scene.control.TextField;
import ru.zeburek.zerohelper.utils.LoggingAdapter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zeburek on 09.06.2017.
 */
public class SearchController {
    private TimerTask highlighterMainTimerTask;
    private Timer highlighterMainTimer = new Timer();

    public void startSearch(JTextPane textPane, TextField textField){
        highlighterMainTimerTask = null;
        String pattern = textField.getText();
        if (!textPane.getText().equals("") && !pattern.equals("")){
            highlighterMainTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int sizeOfPattern = textField.getText().length();
                    if (!textField.getText().equals("") || sizeOfPattern != 0)
                    {
                        highlightWord(textPane, textField);
                    }else{
                        this.cancel();
                        LoggingAdapter.debug("Search","Stopped, due to empty field");
                        removeHighlights(textPane);
                    }

                }
            };

            highlighterMainTimer.schedule(highlighterMainTimerTask, 500, 500);
        }else{
            LoggingAdapter.error("Search","It's empty, as I think: ["+pattern+"]");
        }
    }

    public void highlightWord(JTextPane textComp, TextField textField)
    {
        String patternText = textField.getText().trim();
        // First remove all old highlights
        removeHighlights(textComp);
        LoggingAdapter.debug("Search",splitSearchWords(patternText));
        for (String patternStr:splitSearchWords(patternText)) {
            int sizeOfPattern = patternStr.length();
            if (sizeOfPattern == 0) return;

            try {
                Highlighter h = textComp.getHighlighter();

                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(
                        textComp.getDocument().getText(
                                0, textComp.getDocument().getLength()
                        ));
                boolean matchFound = matcher.matches(); // false
                int paddingFirst = 0;

                if (!matchFound) {
                    while (matcher.find()) {
                        int start = matcher.start() - paddingFirst;
                        int end = matcher.end() - paddingFirst;

                        try {
                            h.addHighlight(start, end, myHighlightPainter);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            } catch (BadLocationException e) {
            }
        }
    }
    public void removeHighlights(JTextPane textComp)
    {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        for (int i=0; i<hilites.length; i++)
        {
            if (hilites[i].getPainter() instanceof MyHighlightPainter)
            {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }

    // An instance of the private subclass of the default highlight painter
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.red);

    // A private subclass of the default highlight painter
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
    {
        public MyHighlightPainter(Color color)
        {
            super(color);
        }
    }

    public String[] splitSearchWords(String searchString){
        return searchString.split("\\|");
    }
}
