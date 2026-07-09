package com.releaseradar.controller;

import com.releaseradar.dto.TrackDto;
import com.releaseradar.repo.TrackRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackRepository repo;

    public TrackController(TrackRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<TrackDto> list() {
        return repo.findAll().stream().map(TrackDto::from).toList();
    }
}
