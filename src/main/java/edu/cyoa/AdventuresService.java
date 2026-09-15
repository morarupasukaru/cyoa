package edu.cyoa;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.cyoa.model.Adventure;
import edu.cyoa.model.Location;
import edu.cyoa.model.Option;

@Service
public class AdventuresService {

    public Optional<String> validationErrorCode(Adventure adventure) {
        if (adventure == null || adventure.getLocations() == null
                || adventure.getStartLocationId() == null) {
            return Optional.of("INVALID_ADVENTURE");
        }

        Map<String, Location> locationsById = new HashMap<>();
        for (Location location : adventure.getLocations()) {
            if (location == null || location.getId() == null
                    || locationsById.put(location.getId(), location) != null) {
                return Optional.of("DUPLICATE_LOCATION_ID");
            }
        }

        Location start = locationsById.get(adventure.getStartLocationId());
        if (start == null) {
            return Optional.of("START_LOCATION_NOT_FOUND");
        }

        boolean hasEnd = false;
        for (Location location : adventure.getLocations()) {
            List<Option> options = location.getOptions();
            if (options == null || options.isEmpty()) {
                hasEnd = true;
                continue;
            }
            for (Option option : options) {
                if (option == null || option.getLocationId() == null
                        || !locationsById.containsKey(option.getLocationId())) {
                    return Optional.of("OPTION_LOCATION_NOT_FOUND");
                }
            }
        }
        if (!hasEnd) {
            return Optional.of("NO_END_LOCATION");
        }

        Set<String> reachable = new HashSet<>();
        ArrayDeque<Location> pending = new ArrayDeque<>();
        pending.add(start);
        while (!pending.isEmpty()) {
            Location location = pending.remove();
            if (!reachable.add(location.getId())) {
                continue;
            }
            List<Option> options = location.getOptions();
            if (options != null) {
                for (Option option : options) {
                    pending.add(locationsById.get(option.getLocationId()));
                }
            }
        }
        return reachable.size() == locationsById.size()
                ? Optional.empty()
                : Optional.of("UNREACHABLE_LOCATION");
    }
}
