package edu.cyoa;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.cyoa.model.Adventure;
import jakarta.validation.Valid;

@RestController 
public class AdventuresResource implements edu.cyoa.AdventuresApi {

    private final AdventuresService adventuresService;
    private final AdventuresBasicService adventuresBasicService;
    private final AdventureSession adventureSession;

    public AdventuresResource(AdventuresService adventuresService,
            AdventuresBasicService adventuresBasicService, AdventureSession adventureSession) {
        this.adventuresService = adventuresService;
        this.adventuresBasicService = adventuresBasicService;
        this.adventureSession = adventureSession;
    }

    @Override
    public ResponseEntity<Adventure> validateAdventure(@Valid Adventure adventure) {
        adventuresService.validationErrorCode(adventure)
                .ifPresent(errorCode -> {
                    throw new InvalidAdventureException(errorCode);
                });
        return ResponseEntity.ok(adventure);
    }

    @PostMapping(value = "/adventures/basic", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportBasic(@Valid @RequestBody Adventure adventure) {
        validate(adventure);
        return basicDownload(adventure);
    }

    @GetMapping(value = "/adventures/basic", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportSessionBasic() {
        Adventure adventure = adventureSession.getAdventure();
        validate(adventure);
        return basicDownload(adventure);
    }

    private void validate(Adventure adventure) {
        adventuresService.validationErrorCode(adventure)
                .ifPresent(errorCode -> {
                    throw new InvalidAdventureException(errorCode);
                });
    }

    private ResponseEntity<String> basicDownload(Adventure adventure) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=adventure.bas")
                .body(adventuresBasicService.toBasic(adventure));
    }

    @ExceptionHandler(InvalidAdventureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAdventure(InvalidAdventureException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getErrorCode()));
    }

    public record ErrorResponse(String errorCode) {
    }

    private static class InvalidAdventureException extends RuntimeException {

        private final String errorCode;

        private InvalidAdventureException(String errorCode) {
            this.errorCode = errorCode;
        }

        private String getErrorCode() {
            return errorCode;
        }
    }
}
