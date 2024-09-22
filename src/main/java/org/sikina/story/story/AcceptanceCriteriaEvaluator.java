package org.sikina.story.story;

import org.sikina.story.time.ApproxDuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class AcceptanceCriteriaEvaluator {
    public boolean passes(PatientStory story, StoryAcceptanceCriteria criteria) {
        if (story.firstOccurance().isEqual(LocalDateTime.MAX)) {
            return false; // observation wasn't found
        }
        LocalDateTime requiredLastDate = add(criteria.after(), story.firstOccurance());
        LocalDateTime requiredFirstDate = subtract(criteria.before(), story.firstOccurance());
        return requiredLastDate.isBefore(story.lastObservation())
            && requiredFirstDate.isAfter(story.firstObservation());
    }

    private LocalDateTime add(ApproxDuration duration, LocalDateTime start) {
        return switch (duration.unit()) {
            case ChronoUnit.DAYS -> start.plusDays(duration.duration());
            case ChronoUnit.WEEKS -> start.plusWeeks(duration.duration());
            case ChronoUnit.MONTHS -> start.plusMonths(duration.duration());
            case ChronoUnit.YEARS -> start.plusYears(duration.duration());
            default -> throw new RuntimeException("Unexpected unit");
        };
    }

    private LocalDateTime subtract(ApproxDuration duration, LocalDateTime start) {
        return switch (duration.unit()) {
            case ChronoUnit.DAYS -> start.minusDays(duration.duration());
            case ChronoUnit.WEEKS -> start.minusWeeks(duration.duration());
            case ChronoUnit.MONTHS -> start.minusMonths(duration.duration());
            case ChronoUnit.YEARS -> start.minusYears(duration.duration());
            default -> throw new RuntimeException("Unexpected unit");
        };
    }
}
