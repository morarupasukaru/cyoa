package edu.cyoa;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import edu.cyoa.model.Option;

@Service
public class AdventuresBasicService {

    public String toBasic(Adventure adventure) {
        Map<String, Integer> lineNumbers = lineNumbersFor(adventure);
        StringBuilder basic = new StringBuilder();
        int lineNumber = 10;

        appendLine(basic, lineNumber, "REM CREATE YOUR OWN ADVENTURE");
        appendLine(basic, lineNumber += 10, "PRINT CHR$(147)");
        appendLine(basic, lineNumber += 10, "GOTO " + lineNumbers.get(adventure.getStartLocationId()));

        for (Location location : adventure.getLocations()) {
            lineNumber = lineNumbers.get(location.getId());
            appendLine(basic, lineNumber, "PRINT CHR$(147)");
            appendLine(basic, lineNumber += 10, "PRINT \"" + escape(location.getText()) + "\"");

            List<Option> options = location.getOptions();
            if (options == null || options.isEmpty()) {
                appendLine(basic, lineNumber += 10, "PRINT \"THE END\"");
                appendLine(basic, lineNumber += 10, "END");
                continue;
            }

            appendLine(basic, lineNumber += 10, "PRINT \"\"");
            for (int index = 0; index < options.size(); index++) {
                appendLine(basic, lineNumber += 10,
                        "PRINT \"" + (index + 1) + ". " + escape(options.get(index).getText()) + "\"");
            }
            appendLine(basic, lineNumber += 10, "INPUT \"CHOICE\";C");
            for (int index = 0; index < options.size(); index++) {
                Option option = options.get(index);
                appendLine(basic, lineNumber += 10,
                        "IF C=" + (index + 1) + " THEN GOTO " + lineNumbers.get(option.getLocationId()));
            }
            appendLine(basic, lineNumber += 10, "GOTO " + lineNumbers.get(location.getId()));
        }
        return basic.toString();
    }

    private Map<String, Integer> lineNumbersFor(Adventure adventure) {
        Map<String, Integer> lineNumbers = new HashMap<>();
        int lineNumber = 100;
        for (Location location : adventure.getLocations()) {
            lineNumbers.put(location.getId(), lineNumber);
            lineNumber += 100;
        }
        return lineNumbers;
    }

    private void appendLine(StringBuilder basic, int lineNumber, String statement) {
        basic.append(lineNumber).append(' ').append(statement).append('\n');
    }

    private String escape(String text) {
        String c64Text = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replace("œ", "oe")
            .replace("Œ", "OE")
            .replace("æ", "ae")
            .replace("Æ", "AE")
            .replace("ß", "ss")
            .replace("“", "\"")
            .replace("”", "\"")
            .replace("’", "'")
            .replace("–", "-")
            .replace("—", "-")
            .replace("…", "...")
            .replaceAll("[^\\x20-\\x7E]", "")
            .toUpperCase(Locale.ROOT);
        return c64Text.replace("\"", "\"\"");
    }
}