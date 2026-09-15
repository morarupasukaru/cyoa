package edu.cyoa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import edu.cyoa.model.Option;

@WebMvcTest(AdventuresResource.class)
@org.springframework.context.annotation.Import(AdventuresBasicService.class)
class AdventuresResourceTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdventuresService adventuresService;

    @MockitoBean
    private AdventureSession adventureSession;

    @BeforeEach
    void serviceAcceptsAdventureByDefault() {
        when(adventuresService.validationErrorCode(any(Adventure.class)))
                .thenReturn(Optional.empty());
    }

    @Test
    void addAdventure_valid() throws Exception {
        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidAdventure())))
                .andExpect(status().isOk());
    }

        @Test
        void exportBasicReturnsDownloadableText() throws Exception {
        Adventure adventure = createValidAdventure();

        mockMvc.perform(post("/adventures/basic").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_PLAIN)
            .content(objectMapper.writeValueAsString(adventure)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(header().string("Content-Disposition", "attachment; filename=adventure.bas"))
                .andExpect(content().string("10 REM CREATE YOUR OWN ADVENTURE\n"
                    + "20 PRINT CHR$(147)\n"
                    + "30 GOTO 100\n"
                    + "100 PRINT \"Start location\"\n"
                    + "110 PRINT \"THE END\"\n"
                    + "120 END\n"));
        }

    @Test
    void exportSessionBasicReturnsDownloadableText() throws Exception {
        when(adventureSession.getAdventure()).thenReturn(createValidAdventure());

        mockMvc.perform(get("/adventures/basic").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(header().string("Content-Disposition", "attachment; filename=adventure.bas"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("10 REM CREATE YOUR OWN ADVENTURE")));
    }

    @Test
    void addAdventure_validReachableGraphWithEnding() throws Exception {
        Adventure adventure = createValidAdventure();
        Location ending = new Location("ending", "The adventure ends.");
        adventure.getLocations().get(0).setOptions(List.of(new Option("Continue", "ending")));
        adventure.getLocations().add(ending);

        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adventure)))
                .andExpect(status().isOk());
    }

    @Test
    void addAdventure_withoutStartLocation() throws Exception {
        Adventure adventure = createValidAdventure();
        adventure.setStartLocationId("missing");
        when(adventuresService.validationErrorCode(any(Adventure.class)))
            .thenReturn(Optional.of("START_LOCATION_NOT_FOUND"));

        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adventure)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("START_LOCATION_NOT_FOUND"));
    }

    @Test
    void addAdventure_withOptionToMissingLocation() throws Exception {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().get(0).setOptions(List.of(new Option("Continue", "missing")));
        when(adventuresService.validationErrorCode(any(Adventure.class)))
            .thenReturn(Optional.of("OPTION_LOCATION_NOT_FOUND"));

        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adventure)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("OPTION_LOCATION_NOT_FOUND"));
    }

    @Test
    void addAdventure_withUnreachableLocation() throws Exception {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().add(new Location("unreachable", "Hidden location"));
        when(adventuresService.validationErrorCode(any(Adventure.class)))
            .thenReturn(Optional.of("UNREACHABLE_LOCATION"));

        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adventure)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("UNREACHABLE_LOCATION"));
    }

    @Test
    void addAdventure_withoutEnding() throws Exception {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().get(0).setOptions(List.of(new Option("Loop", "location1")));
        when(adventuresService.validationErrorCode(any(Adventure.class)))
            .thenReturn(Optional.of("NO_END_LOCATION"));

        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adventure)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("NO_END_LOCATION"));
    }

    @Test
    void addAdventure_withDuplicateLocationIds() throws Exception {
        Adventure adventure = createValidAdventure();
        adventure.getLocations().add(new Location("location1", "Duplicate location"));
        when(adventuresService.validationErrorCode(any(Adventure.class)))
            .thenReturn(Optional.of("DUPLICATE_LOCATION_ID"));

        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adventure)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("DUPLICATE_LOCATION_ID"));
    }

    @Test
    void addAdventure_withoutLocations() throws Exception {
        Adventure invalidAdventure = createValidAdventure();
        invalidAdventure.setLocations(List.of());
        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAdventure)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAdventure_invalidJson1() throws Exception {
        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content("{\"test\": \"invalid\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addAdventure_invalidJson2() throws Exception {
        mockMvc.perform(post("/adventures").contentType(MediaType.APPLICATION_JSON)
                .content("invalid"))
                .andExpect(status().is4xxClientError());
    }

    private Adventure createValidAdventure() {
        Adventure adventure = new Adventure();
        adventure.setId("adventure1");
        adventure.setDescription("A simple adventure");
        String locationId = "location1";
        adventure.setStartLocationId(locationId);

        Location startLocation = new Location();
        startLocation.setId(locationId);
        startLocation.setText("Start location");
        adventure.setLocations(new ArrayList<>(List.of(startLocation)));

        return adventure;
    }
}