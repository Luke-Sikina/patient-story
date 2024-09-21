package org.sikina.story.story;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
class PatientLineParserTest {
    @Autowired
    PatientLineParser subject;

    @Test
    void shouldParseLine() {
        Optional<PatientLine> actual = subject.parse("1,path,,idk,2015-08-06T16:56:28Z");

        PatientLine expected = new PatientLine(1, "path", LocalDateTime.of(2015, 8, 6, 16, 56, 28));

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    void shouldNotParseLineWithBadId() {
        Optional<PatientLine> actual = subject.parse("???,path,,idk,2015-08-06T16:56:28Z");

        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    void shouldNotParseLineWithBadDate() {
        Optional<PatientLine> actual = subject.parse("1,path,,idk,2015123122131-08-06T16:56:28Z");

        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    void shouldNotParseLineWithTooManyCells() {
        Optional<PatientLine> actual = subject.parse("1,path,,idk,2015-08-06T16:56:28Z,im another cell");

        Assertions.assertFalse(actual.isPresent());
    }

}