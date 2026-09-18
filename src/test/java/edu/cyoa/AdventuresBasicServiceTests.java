package edu.cyoa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;

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
}