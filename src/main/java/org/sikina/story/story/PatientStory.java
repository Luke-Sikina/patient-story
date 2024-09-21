package org.sikina.story.story;

import java.time.LocalDateTime;

public record PatientStory(
    int patientId, LocalDateTime firstOccurance, LocalDateTime firstObservation, LocalDateTime lastObservation, int lines
) {
}
