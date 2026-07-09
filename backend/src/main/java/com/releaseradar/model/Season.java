package com.releaseradar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "season")
public class Season {

    @Id
    private Long id;

    @Column(name = "season_number")
    private Integer seasonNumber;

    @Column(name = "num_episodes")
    private Integer numEpisodes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private TvSeries series;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSeasonNumber() { return seasonNumber; }
    public void setSeasonNumber(Integer seasonNumber) { this.seasonNumber = seasonNumber; }
    public Integer getNumEpisodes() { return numEpisodes; }
    public void setNumEpisodes(Integer numEpisodes) { this.numEpisodes = numEpisodes; }
    public TvSeries getSeries() { return series; }
    public void setSeries(TvSeries series) { this.series = series; }
}
