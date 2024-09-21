package org.sikina.story.story;

import java.time.LocalDateTime;

public record PatientLine(int patientId, String path, LocalDateTime timestamp) {
}
