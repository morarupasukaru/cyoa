package edu.cyoa;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import edu.cyoa.model.Adventure;

@Component 
@SessionScope
public class AdventureSession {

    private Adventure adventure;
    private String currentLocation;

    public Adventure getAdventure() {
        return adventure;
    }

    public void setAdventure(Adventure adventure) {
        this.adventure = adventure;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void resetPlay() {
        this.currentLocation = null;
    }
}
