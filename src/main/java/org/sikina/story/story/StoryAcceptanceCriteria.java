package org.sikina.story.story;

import org.sikina.story.time.ApproxDuration;

public record StoryAcceptanceCriteria(String conceptPath, ApproxDuration before, ApproxDuration after) {
}
