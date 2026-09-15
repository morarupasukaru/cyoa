package edu.cyoa;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.web.server.servlet.Session;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import jakarta.annotation.Resource;

@Controller
public class AdventureController {

    @Resource
    AdventureSession adventureSession;

    RestClient restClient = RestClient.create();

    @GetMapping("/")
    public String homePage(Model model) {
        Adventure adventure = adventureSession.getAdventure();
        if (adventure == null) {
            return "upload";
        }
        if (adventureSession.getCurrentLocation() == null) {
            return "redirect:/start";
        } else {
            return "redirect:/locations/" + adventureSession.getCurrentLocation();
        }
    }

    @GetMapping("/start")
    public String startPage(Model model) {
        Adventure adventure = adventureSession.getAdventure();
        if (adventure == null) {
            return "redirect:/";
        }
        model.addAttribute("adventure", adventure);
        model.addAttribute("startLocationId", adventure.getStartLocationId());
        return "start";
    }

    @GetMapping("/locations/{id}")
    public String locations(Model model, @PathVariable("id") String id) {
        Adventure adventure = adventureSession.getAdventure();
        if (adventure == null || Strings.isBlank(id)) {
            return "redirect:/";
        }
        Optional<Location> location = getLocationById(adventure, id);
        if (location.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("adventure", adventureSession.getAdventure());
        model.addAttribute("location", location.get());
        return "locations";
    }

    private Optional<Location> getLocationById(Adventure adventure, String locationId) {
        if (adventure == null || Strings.isBlank(locationId)) {
            return Optional.empty();
        }
        for (Location location : adventure.getLocations()) {
            if (locationId.equals(location.getId())) {
                return Optional.of(location);
            }
        }
        return Optional.empty();
    }

    @GetMapping("/reset")
    public String resetPage() {
        adventureSession.setAdventure(null);
        adventureSession.resetPlay();
        return "redirect:/";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Session session) throws IOException {
        String context = file.getResource().getContentAsString(Charset.defaultCharset());

        ResponseEntity<Adventure> adventureResponse = this.restClient.post()
                .uri("http://localhost:8080/adventures")
                .body(context)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Adventure.class);
        adventureSession.setAdventure(adventureResponse.getBody());
        adventureSession.resetPlay();
        return "redirect:/";
    }
}