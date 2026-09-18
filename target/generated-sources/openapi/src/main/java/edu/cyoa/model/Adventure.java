package edu.cyoa.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import edu.cyoa.model.Location;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Adventure
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-18T10:14:56.695473452+02:00[Europe/Zurich]", comments = "Generator version: 7.25.0")
public class Adventure {

  private String id;

  private String description;

  private String startLocationId;

  private List<@Valid Location> locations = new ArrayList<>();

  public Adventure() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Adventure(String id, String description, String startLocationId) {
    this.id = id;
    this.description = description;
    this.startLocationId = startLocationId;
  }

  public Adventure id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  public Adventure description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @NotNull 
  @Schema(name = "description", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  public Adventure startLocationId(String startLocationId) {
    this.startLocationId = startLocationId;
    return this;
  }

  /**
   * Get startLocationId
   * @return startLocationId
   */
  @NotNull 
  @Schema(name = "startLocationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("startLocationId")
  public String getStartLocationId() {
    return startLocationId;
  }

  @JsonProperty("startLocationId")
  public void setStartLocationId(String startLocationId) {
    this.startLocationId = startLocationId;
  }

  public Adventure locations(List<@Valid Location> locations) {
    this.locations = locations;
    return this;
  }

  public Adventure addLocationsItem(Location locationsItem) {
    if (this.locations == null) {
      this.locations = new ArrayList<>();
    }
    this.locations.add(locationsItem);
    return this;
  }

  /**
   * Get locations
   * @return locations
   */
  @Valid @Size(min = 1) 
  @Schema(name = "locations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locations")
  public List<@Valid Location> getLocations() {
    return locations;
  }

  @JsonProperty("locations")
  public void setLocations(List<@Valid Location> locations) {
    this.locations = locations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Adventure adventure = (Adventure) o;
    return Objects.equals(this.id, adventure.id) &&
        Objects.equals(this.description, adventure.description) &&
        Objects.equals(this.startLocationId, adventure.startLocationId) &&
        Objects.equals(this.locations, adventure.locations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, startLocationId, locations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Adventure {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    startLocationId: ").append(toIndentedString(startLocationId)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

