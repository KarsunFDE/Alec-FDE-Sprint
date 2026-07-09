package com.releaseradar.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "track")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "media_item_id")
    private MediaItem mediaItem;

    @Column(name = "date_tracked")
    private LocalDate dateTracked;

    @Column(name = "is_notified")
    private boolean notified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public MediaItem getMediaItem() { return mediaItem; }
    public void setMediaItem(MediaItem mediaItem) { this.mediaItem = mediaItem; }
    public LocalDate getDateTracked() { return dateTracked; }
    public void setDateTracked(LocalDate dateTracked) { this.dateTracked = dateTracked; }
    public boolean isNotified() { return notified; }
    public void setNotified(boolean notified) { this.notified = notified; }
}
