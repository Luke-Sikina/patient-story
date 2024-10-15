package org.sikina.story.timeline;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public record PatientEvent(LocalDateTime time, String path) {

    public String parseCategory() {
        return Stream.of(path.split("\\\\"))
            .filter(StringUtils::hasLength)
            .toList()
            .getFirst();
    }

    public String parseConcept() {
        return Stream.of(path.split("\\\\"))
            .filter(StringUtils::hasLength)
            .toList()
            .getLast();
    }
}
