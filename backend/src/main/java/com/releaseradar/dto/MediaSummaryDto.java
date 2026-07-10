package com.releaseradar.dto;

import com.releaseradar.model.MediaItem;

import java.time.LocalDate;
import java.util.List;

public class MediaSummaryDto {
    public Long id;
    public String title;
    public LocalDate releaseDate;
    public String type;
    public String genre;
    public List<String> tags;

    public static MediaSummaryDto from(MediaItem m) {
        MediaSummaryDto d = new MediaSummaryDto();
        d.id = m.getId();
        d.title = m.getTitle();
        d.releaseDate = m.getReleaseDate();
        d.type = m.getType();
        d.genre = m.getGenre();
        d.tags = m.getTags().stream().map(t -> t.getName()).toList();
        return d;
    }
}
