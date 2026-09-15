package edu.cyoa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import edu.cyoa.model.Option;

class AdventuresServiceTests {

    private final AdventuresService adventuresService = new AdventuresService();

    @Test
    void validAdventureHasNoValidationError() {
        assertTrue(adventuresService.validationErrorCode(createValidAdventure()).isEmpty());
    }

    @Test
    void nullAdventureIsInvalid() {
        assertErrorCode(null, "INVALID_ADVENTURE");
    }

    @Test
    void duplicateLocationIdIsInvalid() {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().add(new Location("start", "Duplicate"));

        assertErrorCode(adventure, "DUPLICATE_LOCATION_ID");
    }

    @Test
    void missingStartLocationIsInvalid() {
        Adventure adventure = createValidAdventure();
        adventure.setStartLocationId("missing");

        assertErrorCode(adventure, "START_LOCATION_NOT_FOUND");
    }

    @Test
    void optionToMissingLocationIsInvalid() {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().get(0).setOptions(List.of(new Option("Continue", "missing")));

        assertErrorCode(adventure, "OPTION_LOCATION_NOT_FOUND");
    }

    @Test
    void adventureWithoutEndingIsInvalid() {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().get(0).setOptions(List.of(new Option("Loop", "start")));

        assertErrorCode(adventure, "NO_END_LOCATION");
    }

    @Test
    void unreachableLocationIsInvalid() {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().add(new Location("hidden", "Hidden location"));

        assertErrorCode(adventure, "UNREACHABLE_LOCATION");
    }

    @Test
    void reachableAdventureWithEndingIsValid() {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().get(0).setOptions(List.of(new Option("Continue", "ending")));
        adventure.getLocations().add(new Location("ending", "The adventure ends."));

        assertTrue(adventuresService.validationErrorCode(adventure).isEmpty());
    }

    private void assertErrorCode(Adventure adventure, String expectedErrorCode) {
        Optional<String> errorCode = adventuresService.validationErrorCode(adventure);

        assertEquals(Optional.of(expectedErrorCode), errorCode);
    }

    private Adventure createValidAdventure() {
        Adventure adventure = new Adventure("adventure1", "A simple adventure", "start");
        adventure.setLocations(new ArrayList<>(List.of(
                new Location("start", "Start location"))));
        return adventure;
    }
}
