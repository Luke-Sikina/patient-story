package org.sikina.story.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PatientStoryProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PatientStoryProcessor.class);

    private final String inputPath, outputPath;
    private final AcceptanceCriteriaEvaluator evaluator;
    private final PatientLineParser lineParser;

    @Autowired
    public PatientStoryProcessor(
        @Value("${input.path}") String inputPath,
        @Value("${output.path}") String outputPath,
        AcceptanceCriteriaEvaluator evaluator, PatientLineParser lineParser
    ) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.evaluator = evaluator;
        this.lineParser = lineParser;
    }

    public void process(StoryAcceptanceCriteria criteria) {
        LOG.info("Parsing CSV {} and outputting to {}", inputPath, outputPath);
        File inputFile = new File(inputPath);
        if (inputFile.isDirectory() || !inputFile.exists()) {
            LOG.error("Input file {} DNE or is dir", inputPath);
            return;
        }
        try (FileOutputStream o = new FileOutputStream(outputPath)) {
            processStories(criteria, inputFile, o);
        } catch (IOException e) {
            LOG.error("Error creating output ");
        }

        LOG.info("Done reading patients");
    }

    private void processStories(StoryAcceptanceCriteria criteria, File inputFile, FileOutputStream outputStream) {
        int currentPatient = -1;
        int linesProcessed = 0;
        LocalDateTime firstObservation = LocalDateTime.MAX;
        LocalDateTime lastObservation = LocalDateTime.MIN;
        LocalDateTime earliestMatchingCode = LocalDateTime.MAX;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Optional<PatientLine> maybePatient = lineParser.parse(line);
                if (maybePatient.isPresent()) {
                    int patient = maybePatient.get().patientId();
                    if (currentPatient != patient) {
                        LOG.info("Processed {} lines for patient {}", linesProcessed, patient);
                        PatientStory story = new PatientStory(currentPatient, earliestMatchingCode, firstObservation, lastObservation, linesProcessed);
                        writePatient(story, outputStream, criteria);

                        LOG.info("Starting new patient.");
                        currentPatient = patient;
                        linesProcessed = 0;
                        firstObservation = LocalDateTime.MAX;
                        lastObservation = LocalDateTime.MIN;
                        earliestMatchingCode = LocalDateTime.MAX;
                    }
                    LocalDateTime timestamp = maybePatient.get().timestamp();
                    if (maybePatient.get().path().contains(criteria.conceptPath()) && earliestMatchingCode.isAfter(timestamp)) {
                        earliestMatchingCode = timestamp;
                    }
                    if (firstObservation.isAfter(timestamp)) {
                        firstObservation = timestamp;
                    }
                    if (lastObservation.isBefore(timestamp)) {
                        lastObservation = timestamp;
                    }
                    linesProcessed++;
                }
            }

            PatientStory story = new PatientStory(currentPatient, earliestMatchingCode, firstObservation, lastObservation, linesProcessed);
            if (evaluator.passes(story, criteria)) {
                writePatient(story, outputStream, criteria);
            }
        } catch (IOException e) {
            LOG.error("Error reading from CSV", e);
        }
    }

    private void writePatient(PatientStory story, FileOutputStream outputStream, StoryAcceptanceCriteria criteria) {
        if (evaluator.passes(story, criteria)) {
            LOG.info("Patient {} passes!",story.patientId());
            String line = """
                Patient: %d
                    Date of first observation: %s
                    Date of first matching code: %s
                    Date of last observation: %s
                    Total observations: %d
            """.formatted(story.patientId(), story.firstObservation(), story.firstOccurance(), story.lastObservation(), story.lines());
            try {
                outputStream.write(line.getBytes());
            } catch (IOException e) {
                LOG.error("Error writing patient {} out: ", story.patientId(), e);
            }
        }

    }
}
