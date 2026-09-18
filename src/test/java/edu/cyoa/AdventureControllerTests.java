package edu.cyoa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import edu.cyoa.model.Option;

@WebMvcTest(AdventureController.class)
@Import(AdventureSession.class)
class AdventureControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdventureSession adventureSession;

    @Test
    void homePageRedirectToUpload() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("upload"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Choose an adventure file")));
    }

    @Test
    void homePageWithAdventureRedirectsToStart() throws Exception {
        when(adventureSession.getAdventure()).thenReturn(createAdventure());

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/start"));
    }

    @Test
    void homePageWithCurrentLocationResumesAdventure() throws Exception {
        when(adventureSession.getAdventure()).thenReturn(createAdventure());
        when(adventureSession.getCurrentLocation()).thenReturn("second");

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locations/second"));
    }

    @Test
    void startPageWithoutAdventureRedirectsHome() throws Exception {
        mockMvc.perform(get("/start"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void startPageRendersAdventureDescription() throws Exception {
        when(adventureSession.getAdventure()).thenReturn(createAdventure());

        mockMvc.perform(get("/start"))
                .andExpect(status().isOk())
                .andExpect(view().name("start"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A simple adventure")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/locations/start")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/adventures/basic")));
    }

    @Test
    void locationWithoutAdventureRedirectsHome() throws Exception {
        mockMvc.perform(get("/locations/start"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void locationWithOptionsRendersChoices() throws Exception {
        when(adventureSession.getAdventure()).thenReturn(createAdventure());

        mockMvc.perform(get("/locations/start"))
                .andExpect(status().isOk())
                .andExpect(view().name("locations"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("You stand at the trailhead.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Take the forest path")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/locations/second")));
    }

    @Test
    void locationTextPreservesNewlines() throws Exception {
        Adventure adventure = createAdventure();
        adventure.getLocations().get(0).setText("First line\nSecond line");
        when(adventureSession.getAdventure()).thenReturn(adventure);

        mockMvc.perform(get("/locations/start"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "First line\nSecond line")));
    }

    @Test
    void unknownLocationRedirectsHome() throws Exception {
        when(adventureSession.getAdventure()).thenReturn(createAdventure());

        mockMvc.perform(get("/locations/unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void resetClearsAdventureAndRedirectsHome() throws Exception {
        mockMvc.perform(get("/reset"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(adventureSession).setAdventure(null);
        verify(adventureSession).resetPlay();
    }

    private Adventure createAdventure() {
        Location start = new Location("start", "You stand at the trailhead.");
        start.setOptions(List.of(new Option("Take the forest path", "second")));
        Location second = new Location("second", "The forest is quiet.");

        return new Adventure("adventure1", "A simple adventure", "start")
                .locations(List.of(start, second));
    }
}