package org.sikina.story.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DurationParser {

    private static final Logger LOG = LoggerFactory.getLogger(DurationParser.class);

    private static final Pattern daysPattern = Pattern.compile("^(\\d+)([dD])$");
    private static final Pattern weeksPattern = Pattern.compile("^(\\d+)([wW])$");
    private static final Pattern monthsPattern = Pattern.compile("^(\\d+)([mM])$");
    private static final Pattern yearsPatten = Pattern.compile("^(\\d+)([yY])$");

    private record PatternUnit(TemporalUnit unit, Pattern pattern){};
    private static final List<PatternUnit> patterns = List.of(
        new PatternUnit(ChronoUnit.DAYS, daysPattern),
        new PatternUnit(ChronoUnit.WEEKS, weeksPattern),
        new PatternUnit(ChronoUnit.MONTHS, monthsPattern),
        new PatternUnit(ChronoUnit.YEARS, yearsPatten)
    );

    public Optional<ApproxDuration> toDuration(String raw) {
        for (PatternUnit p : patterns) {
            Matcher matcher = p.pattern.matcher(raw);
            if (matcher.find()) {
                long duration = Long.parseLong(matcher.group(1));
                return Optional.of(new ApproxDuration(p.unit, duration));
            }
        }
        LOG.error("Failed to parse duration from {}", raw);
        return Optional.empty();
    }
}
