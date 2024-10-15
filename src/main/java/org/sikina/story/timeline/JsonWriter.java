package org.sikina.story.timeline;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JsonWriter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonWriter.class);

    public void write(Path out, Timeline in) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(out.toFile(), toJson(in));
        } catch (IOException e) {
            LOG.error("Couldn't make file", e);
        }
    }

    private Map<String, Object> toJson(Timeline in) {
        Map<String, List<PatientEvent>> groupedEvents = in.events()
            .stream()
            .collect(Collectors.groupingBy(PatientEvent::parseCategory));

        List<Map<String, Object>> categories = groupedEvents.entrySet().stream()
            .map(category -> {
                TimelineTrack track = new TimelineTrack(category.getValue());
                return Map.of("label", category.getKey(), "times", track);
            })
            .toList();

        Map<String, Object> json = Map.of("patient", in.patient(), "timeline", categories);
    }




}
