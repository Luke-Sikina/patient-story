package org.sikina.story;

import org.sikina.story.story.PatientStoryProcessor;
import org.sikina.story.story.StoryAcceptanceCriteria;
import org.sikina.story.time.ApproxDuration;
import org.sikina.story.time.DurationParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class Main implements CommandLineRunner {
    
    private static Logger LOG = LoggerFactory.getLogger(Main.class);

    private final PatientStoryProcessor storyProcessor;
    private final DurationParser durationParser;

    @Autowired
    public Main(PatientStoryProcessor storyProcessor, DurationParser durationParser) {
        this.storyProcessor = storyProcessor;
        this.durationParser = durationParser;
    }

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(Main.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        if (args.length != 3) {
            LOG.error("Expected 3 args: concept, time before, time after. Got {}", args.length);
            return;
        }
        String conceptPath = args[0];
        Optional<ApproxDuration> timeBeforeRequiredConcept = durationParser.toDuration(args[1]);
        Optional<ApproxDuration> timeAfterRequiredConcept = durationParser.toDuration(args[2]);
        if (timeBeforeRequiredConcept.isEmpty() || timeAfterRequiredConcept.isEmpty()) {
            return;
        }

        StoryAcceptanceCriteria criteria =
            new StoryAcceptanceCriteria(conceptPath, timeBeforeRequiredConcept.get(), timeAfterRequiredConcept.get());
        LOG.info(
            """
            
            Finding patients with:
                concept path: {}
                at least {} {} of medical history BEFORE the first instance of that code
                at least {} {} of medical history AFTER the first instance of that code
            """,
            conceptPath,
            criteria.before().duration(), criteria.before().unit(),
            criteria.after().duration(), criteria.after().unit()
        );

        storyProcessor.process(criteria);

    }
}
