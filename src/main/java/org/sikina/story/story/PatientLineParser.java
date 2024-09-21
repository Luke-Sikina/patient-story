package org.sikina.story.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Component
public class PatientLineParser {

    private static final Logger LOG = LoggerFactory.getLogger(PatientLineParser.class);

    public Optional<PatientLine> parse(String line) {
        String[] cells = line.split(",");// forgive me, CSV gods
        if (cells.length == 5) {
            try {
                int patient = Integer.parseInt(cells[0]);
                String concept = cells[1];
                LocalDateTime timestamp = LocalDateTime.parse(cells[4].substring(0, cells[4].length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return Optional.of(new PatientLine(patient, concept, timestamp));
            } catch (NumberFormatException | DateTimeParseException e) {
                LOG.warn("Error parsing", e);
            }
        }
        LOG.warn("Found weird CSV line, will discard: \n\t{}", line);
        return Optional.empty();
    }
}
