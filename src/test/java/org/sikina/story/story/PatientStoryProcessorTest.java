package org.sikina.story.story;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sikina.story.time.ApproxDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class PatientStoryProcessorTest {

    @Autowired
    PatientStoryProcessor subject;

    @Test
    void shouldProcessSimpleFile(@TempDir File tmp) throws IOException {
        String fileText = """
        1,foo,,idk,2000-01-01T00:00:00Z
        1,bar,,idk,2005-01-01T00:00:00Z
        1,\\the\\concept\\you\\want\\,,idk,2010-01-01T00:00:00Z
        1,baz,,idk,2015-01-01T00:00:00Z
        1,qux,,idk,2020-01-01T00:00:00Z
        """;
        Path inputPath = Path.of(tmp.getAbsolutePath(), "in.csv");
        Path outputPath = Path.of(tmp.getAbsolutePath(), "out.txt");
        Files.writeString(Path.of(tmp.getAbsolutePath(), "in.csv"), fileText);
        ReflectionTestUtils.setField(subject, "inputPath", inputPath.toString());
        ReflectionTestUtils.setField(subject, "outputPath", outputPath.toString());

        StoryAcceptanceCriteria criteria = new StoryAcceptanceCriteria(
            "concept\\you\\want",
            new ApproxDuration(ChronoUnit.YEARS, 2),
            new ApproxDuration(ChronoUnit.YEARS, 2)
        );
        subject.process(criteria);

        String actual = Files.readString(outputPath);
        String expected = """
                Patient: 1
                    Date of first observation: 2000-01-01T00:00
                    Date of first matching code: 2010-01-01T00:00
                    Date of last observation: 2020-01-01T00:00
                    Total observations: 5
            """;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void shouldProcessMultipleMatchesFile(@TempDir File tmp) throws IOException {
        String fileText = """
        1,foo,,idk,2000-01-01T00:00:00Z
        1,bar,,idk,2005-01-01T00:00:00Z
        1,\\the\\concept\\you\\want\\,,idk,2010-01-01T00:00:00Z
        1,\\the\\concept\\you\\want\\,,idk,2009-01-01T00:00:00Z
        1,baz,,idk,2015-01-01T00:00:00Z
        1,qux,,idk,2020-01-01T00:00:00Z
        """;

        Path inputPath = Path.of(tmp.getAbsolutePath(), "in.csv");
        Path outputPath = Path.of(tmp.getAbsolutePath(), "out.txt");
        Files.writeString(Path.of(tmp.getAbsolutePath(), "in.csv"), fileText);
        ReflectionTestUtils.setField(subject, "inputPath", inputPath.toString());
        ReflectionTestUtils.setField(subject, "outputPath", outputPath.toString());

        StoryAcceptanceCriteria criteria = new StoryAcceptanceCriteria(
            "concept\\you\\want",
            new ApproxDuration(ChronoUnit.YEARS, 2),
            new ApproxDuration(ChronoUnit.YEARS, 2)
        );
        subject.process(criteria);

        String actual = Files.readString(outputPath);
        String expected = """
                Patient: 1
                    Date of first observation: 2000-01-01T00:00
                    Date of first matching code: 2009-01-01T00:00
                    Date of last observation: 2020-01-01T00:00
                    Total observations: 6
            """;
        Assertions.assertEquals(expected, actual);
    }


    @Test
    void shouldProcessMultiplePatientsFile(@TempDir File tmp) throws IOException {
        String fileText = """
        1,foo,,idk,2000-01-01T00:00:00Z
        1,bar,,idk,2005-01-01T00:00:00Z
        1,\\the\\concept\\you\\want\\,,idk,2010-01-01T00:00:00Z
        1,baz,,idk,2015-01-01T00:00:00Z
        1,qux,,idk,2020-01-01T00:00:00Z
        2,foo,,idk,2000-01-01T00:00:00Z
        2,bar,,idk,2005-01-01T00:00:00Z
        2,\\the\\concept\\you\\want\\,,idk,2009-01-01T00:00:00Z
        2,baz,,idk,2015-01-01T00:00:00Z
        2,qux,,idk,2020-01-01T00:00:00Z
        """;

        Path inputPath = Path.of(tmp.getAbsolutePath(), "in.csv");
        Path outputPath = Path.of(tmp.getAbsolutePath(), "out.txt");
        Files.writeString(Path.of(tmp.getAbsolutePath(), "in.csv"), fileText);
        ReflectionTestUtils.setField(subject, "inputPath", inputPath.toString());
        ReflectionTestUtils.setField(subject, "outputPath", outputPath.toString());

        StoryAcceptanceCriteria criteria = new StoryAcceptanceCriteria(
            "concept\\you\\want",
            new ApproxDuration(ChronoUnit.YEARS, 2),
            new ApproxDuration(ChronoUnit.YEARS, 2)
        );
        subject.process(criteria);

        String actual = Files.readString(outputPath);
        String expected = """
                Patient: 1
                    Date of first observation: 2000-01-01T00:00
                    Date of first matching code: 2010-01-01T00:00
                    Date of last observation: 2020-01-01T00:00
                    Total observations: 5
                Patient: 2
                    Date of first observation: 2000-01-01T00:00
                    Date of first matching code: 2009-01-01T00:00
                    Date of last observation: 2020-01-01T00:00
                    Total observations: 5
            """;
        Assertions.assertEquals(expected, actual);
    }
}