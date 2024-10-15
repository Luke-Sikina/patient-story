package org.sikina.story.timeline;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public record TimelineTrack (String label, List<Time> times) {

    private record Time (long starting_time, long ending_time) {}

    public TimelineTrack(List<PatientEvent> events) {
        this(events.getFirst().parseCategory(), createTimes(events));
    }

    private static List<Time> createTimes(List<PatientEvent> events) {
        List<Long> times = events.stream()
            .map(PatientEvent::time)
            .map(t -> t.atZone(ZoneId.systemDefault()).toEpochSecond())
            .sorted()
            .toList();
        long start = times.getFirst() == null ? 0 : times.getFirst();
        return times.stream()
            .map(t -> t - start)
            .map(t -> new Time(t, t))
            .toList();
    }
}

