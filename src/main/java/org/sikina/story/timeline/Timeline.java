package org.sikina.story.timeline;

import java.util.List;

public record Timeline(List<PatientEvent> events, String patient) {

}
