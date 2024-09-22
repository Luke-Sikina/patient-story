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

    private static final LocalDateTime EARLIEST_POSSIBLE_DATE = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final LocalDateTime LATEST_POSSIBLE_DATE = LocalDateTime.now();

    private static final Logger LOG = LoggerFactory.getLogger(PatientLineParser.class);

    public Optional<PatientLine> parse(String line) {
        String[] cells = line.split(",");
        if (cells.length >= 5) {
            try {
                /*
                 The input CSV is not properly formatted. There can be commas in the values for cell[1] and cell[3],
                 and they will not be properly escaped. As a result, the number of cells per row is not stable. The
                 first cell will always be patient #, and the last cell will always be date. Past that, you aren't
                 guaranteed to find what you need at any specific cell index.

                 Since we're doing a simple String.contains() to establish whether a line matches the concept we want,
                 I fight against this by matching against the entire line rather than just the concept path.
                */
                int patient = Integer.parseInt(cells[0]);
                String date = cells[cells.length - 1];
                LocalDateTime timestamp = LocalDateTime.parse(date.substring(0, date.length() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (line.contains("Demographic")) {
                    // Ignore demographic data when looking for data points. The datetime data is wrong
                    return Optional.empty();
                }
                if (timestamp.isBefore(EARLIEST_POSSIBLE_DATE) || timestamp.isAfter(LATEST_POSSIBLE_DATE)) {
                    // I was seeing some dates in the 3700s. This logic just safeguards against those outliers
                    LOG.warn("Excluding row due to bad date:\n\t{}", line);
                    return Optional.empty();
                }
                return Optional.of(new PatientLine(patient, line, timestamp));
            } catch (NumberFormatException | DateTimeParseException e) {
                LOG.warn("Error parsing", e);
            }
        }
        LOG.warn("Found weird CSV line, will discard: \n\t{}", line);
        return Optional.empty();
    }
}
