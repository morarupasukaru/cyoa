package edu.cyoa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import edu.cyoa.model.Option;

class AdventuresBasicServiceTests {

    private final AdventuresBasicService adventuresBasicService = new AdventuresBasicService();

    @Test
    void multilineLocationTextIsExportedAsSeparatePrintLines() {
        Adventure adventure = new Adventure("adventure1", "A simple adventure", "start")
                .locations(List.of(new Location("start", "First line\nSecond line")));

        assertEquals("10 REM CHOOSE YOUR OWN ADVENTURE\n"
                + "20 PRINT CHR$(147)\n"
                + "30 GOTO 100\n"
                + "100 PRINT CHR$(147)\n"
                + "110 PRINT \"FIRST LINE\"\n"
                + "120 PRINT \"SECOND LINE\"\n"
                + "130 PRINT \"THE END\"\n"
                + "140 END\n", adventuresBasicService.toBasic(adventure));
    }

    @Test
    void destinationLineNumbersDoNotOverlapLongLocationBlocks() {
        Adventure adventure = new Adventure("adventure1", "A simple adventure", "first")
                .locations(List.of(
                        new Location("first", "First location")
                                .options(List.of(
                                        new Option("One", "second"),
                                        new Option("Two", "second"),
                                        new Option("Three", "second"),
                                        new Option("Four", "second"))),
                        new Location("second", "Second location")));

        assertEquals("10 REM CHOOSE YOUR OWN ADVENTURE\n"
                + "20 PRINT CHR$(147)\n"
                + "30 GOTO 100\n"
                + "100 PRINT CHR$(147)\n"
                + "110 PRINT \"FIRST LOCATION\"\n"
                + "120 PRINT \"\"\n"
                + "130 PRINT \"1. ONE\"\n"
                + "140 PRINT \"2. TWO\"\n"
                + "150 PRINT \"3. THREE\"\n"
                + "160 PRINT \"4. FOUR\"\n"
                + "170 INPUT \"CHOICE\";C\n"
                + "180 IF C=1 THEN GOTO 230\n"
                + "190 IF C=2 THEN GOTO 230\n"
                + "200 IF C=3 THEN GOTO 230\n"
                + "210 IF C=4 THEN GOTO 230\n"
                + "220 GOTO 100\n"
                + "230 PRINT CHR$(147)\n"
                + "240 PRINT \"SECOND LOCATION\"\n"
                + "250 PRINT \"THE END\"\n"
                + "260 END\n", adventuresBasicService.toBasic(adventure));
    }
}