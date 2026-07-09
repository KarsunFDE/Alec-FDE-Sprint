package com.releaseradar.dto;

import com.releaseradar.model.MediaItem;

import java.time.LocalDate;
import java.util.List;

public class MediaDetailDto {
    public Long id;
    public String title;
    public LocalDate releaseDate;
    public String type;
    public String genre;
    public String description;
    public List<String> tags;
    public List<AssetDto> assets;

    public static class AssetDto {
        public String url;
        public String type;
    }

    public static MediaDetailDto from(MediaItem m) {
        MediaDetailDto d = new MediaDetailDto();
        d.id = m.getId();
        d.title = m.getTitle();
        d.releaseDate = m.getReleaseDate();
        d.type = m.getType();
        d.genre = m.getGenre();
        d.description = m.getDescription();
        d.tags = m.getTags().stream().map(t -> t.getName()).toList();
        d.assets = m.getAssets().stream().map(a -> {
            AssetDto ad = new AssetDto();
            ad.url = a.getUrl();
            ad.type = a.getType();
            return ad;
        }).toList();
        return d;
    }
}
