package org.sikina.story;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sikina.story.story.PatientStoryProcessor;
import org.sikina.story.story.StoryAcceptanceCriteria;
import org.sikina.story.time.ApproxDuration;
import org.sikina.story.time.DurationParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

@SpringBootTest
class MainTest {

    @MockBean
    PatientStoryProcessor patientStoryProcessor;

    @MockBean
    DurationParser durationParser;

    @Autowired
    Main subject;

    @Test
    void shouldRun() {
        ApproxDuration before = new ApproxDuration(ChronoUnit.DAYS, 1);
        ApproxDuration after = new ApproxDuration(ChronoUnit.YEARS, 1);
        Mockito.when(durationParser.toDuration("1d"))
            .thenReturn(Optional.of(before));
        Mockito.when(durationParser.toDuration("1Y"))
            .thenReturn(Optional.of(after));

        subject.run("my cool path", "1d", "1Y");

        StoryAcceptanceCriteria criteria = new StoryAcceptanceCriteria("my cool path", before, after);
        Mockito.verify(patientStoryProcessor)
            .process(criteria);
    }

    @Test
    void shouldNotRunIfBadArgs() {
        subject.run("my cool path", "1d", "1Y", "extra");
        Mockito.verify(patientStoryProcessor, Mockito.never()).process(Mockito.any());
    }

    @Test
    void shouldNotRunIfBadDuration() {
        ApproxDuration before = new ApproxDuration(ChronoUnit.DAYS, 1);
        Mockito.when(durationParser.toDuration("1d"))
            .thenReturn(Optional.of(before));
        Mockito.when(durationParser.toDuration("1z"))
            .thenReturn(Optional.empty());

        subject.run("my cool path", "1d", "1z");

        Mockito.verify(patientStoryProcessor, Mockito.never()).process(Mockito.any());
    }
}