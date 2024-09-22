package org.sikina.story.story;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sikina.story.time.ApproxDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class AcceptanceCriteriaEvaluatorTest {

    @Autowired
    AcceptanceCriteriaEvaluator subject;

    @Test
    void shouldPass() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.DAYS, 2), new ApproxDuration(ChronoUnit.DAYS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.of(2000, 1, 10, 0, 0);
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime lastObservation = LocalDateTime.of(2000, 1, 30, 0, 0);
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertTrue(subject.passes(story, criteria));
    }

    @Test
    void shouldPassWeeks() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.WEEKS, 2), new ApproxDuration(ChronoUnit.WEEKS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.of(2000, 2, 1, 0, 0);
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime lastObservation = LocalDateTime.of(2000, 3, 1, 0, 0);
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertTrue(subject.passes(story, criteria));
    }

    @Test
    void shouldPassMonths() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.MONTHS, 2), new ApproxDuration(ChronoUnit.MONTHS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.of(2000, 4, 1, 0, 0);
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime lastObservation = LocalDateTime.of(2000, 7, 1, 0, 0);
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertTrue(subject.passes(story, criteria));
    }

    @Test
    void shouldFailNoDiagnosis() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.DAYS, 2), new ApproxDuration(ChronoUnit.DAYS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.MAX;
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime lastObservation = LocalDateTime.of(2000, 1, 30, 0, 0);
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertFalse(subject.passes(story, criteria));
    }

    @Test
    void shouldFailNotEnoughBefore() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.DAYS, 2), new ApproxDuration(ChronoUnit.DAYS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.of(2000, 1, 10, 0, 0);
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 9, 0, 0); // 1 day before
        LocalDateTime lastObservation = LocalDateTime.of(2000, 1, 30, 0, 0);
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertFalse(subject.passes(story, criteria));
    }

    @Test
    void shouldFailNotEnoughAfter() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.DAYS, 2), new ApproxDuration(ChronoUnit.DAYS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.of(2000, 1, 10, 0, 0);
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime lastObservation = LocalDateTime.of(2000, 1, 11, 0, 0); // 1 day after
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertFalse(subject.passes(story, criteria));
    }

    @Test
    void shouldExplodeForBadUnit() {
        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria("", new ApproxDuration(ChronoUnit.HALF_DAYS, 2), new ApproxDuration(ChronoUnit.HALF_DAYS, 2));

        LocalDateTime firstDiagnosis = LocalDateTime.of(2000, 1, 10, 0, 0);
        LocalDateTime firstObservation = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime lastObservation = LocalDateTime.of(2000, 1, 11, 0, 0); // 1 day after
        PatientStory story = new PatientStory(1, firstDiagnosis, firstObservation, lastObservation, 999);

        Assertions.assertThrows(RuntimeException.class, () -> subject.passes(story, criteria));
    }
}