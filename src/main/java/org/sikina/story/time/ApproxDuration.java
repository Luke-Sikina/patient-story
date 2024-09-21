package org.sikina.story.time;

import java.time.temporal.TemporalUnit;

public record ApproxDuration(TemporalUnit unit, long duration) {
}
