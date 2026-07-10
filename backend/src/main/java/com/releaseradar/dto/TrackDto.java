package com.releaseradar.dto;

import com.releaseradar.model.Track;
import com.releaseradar.model.User;

import java.time.LocalDate;

public class TrackDto {
    public Long id;
    public User user;
    public Long mediaItemId;
    public String mediaTitle;
    public LocalDate dateTracked;
    public boolean notified;

    public static TrackDto from(Track t) {
        TrackDto d = new TrackDto();
        d.id = t.getId();
        d.user = t.getUser();
        d.mediaItemId = t.getMediaItem().getId();
        d.mediaTitle = t.getMediaItem().getTitle();
        d.dateTracked = t.getDateTracked();
        d.notified = t.isNotified();
        return d;
    }
}
