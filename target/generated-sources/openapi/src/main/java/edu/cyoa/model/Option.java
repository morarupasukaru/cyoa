package edu.cyoa.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Option
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-15T07:38:44.728466529+02:00[Europe/Zurich]", comments = "Generator version: 7.25.0")
public class Option {

  private String text;

  private String locationId;

  public Option() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Option(String text, String locationId) {
    this.text = text;
    this.locationId = locationId;
  }

  public Option text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
   */
  @NotNull 
  @Schema(name = "text", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  @JsonProperty("text")
  public void setText(String text) {
    this.text = text;
  }

  public Option locationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  /**
   * Get locationId
   * @return locationId
   */
  @NotNull 
  @Schema(name = "locationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("locationId")
  public String getLocationId() {
    return locationId;
  }

  @JsonProperty("locationId")
  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Option option = (Option) o;
    return Objects.equals(this.text, option.text) &&
        Objects.equals(this.locationId, option.locationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, locationId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Option {\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    locationId: ").append(toIndentedString(locationId)).append("\n");
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

