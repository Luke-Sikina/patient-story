package org.sikina.story.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@SpringBootTest
class DurationParserTest {
    @Autowired
    DurationParser subject;

    @Test
    void shouldParseDay() {
        Optional<ApproxDuration> actual = subject.toDuration("1d");
        ApproxDuration expected = new ApproxDuration(ChronoUnit.DAYS, 1);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldParseWeek() {
        Optional<ApproxDuration> actual = subject.toDuration("52w");
        ApproxDuration expected = new ApproxDuration(ChronoUnit.WEEKS, 52);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldParseMonth() {
        Optional<ApproxDuration> actual = subject.toDuration("22m");
        ApproxDuration expected = new ApproxDuration(ChronoUnit.MONTHS, 22);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldParseYears() {
        Optional<ApproxDuration> actual = subject.toDuration("31y");
        ApproxDuration expected = new ApproxDuration(ChronoUnit.YEARS, 31);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldNotParse() {
        Optional<ApproxDuration> actual = subject.toDuration("123z");
        Optional<ApproxDuration> expected = Optional.empty();

        Assertions.assertEquals(expected, actual);
    }
}