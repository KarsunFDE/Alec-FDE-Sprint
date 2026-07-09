package com.releaseradar.controller;

import com.releaseradar.dto.MediaDetailDto;
import com.releaseradar.dto.MediaSummaryDto;
import com.releaseradar.model.MediaItem;
import com.releaseradar.repo.MediaItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaItemRepository repo;

    public MediaController(MediaItemRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<MediaSummaryDto> list(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        int offset = (page - 1) * size + 1;
        List<MediaItem> items = repo.search(q, size, offset);
        return items.stream().map(MediaSummaryDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaDetailDto> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(m -> ResponseEntity.ok(MediaDetailDto.from(m)))
                .orElse(ResponseEntity.ok(null));
    }

    @PostMapping
    public ResponseEntity<MediaItem> create(@RequestBody MediaItem body) {
        body.setId(null);
        MediaItem saved = repo.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MediaItem> update(@PathVariable Long id, @RequestBody MediaItem body) {
        return repo.findById(id).map(existing -> {
            existing.setTitle(body.getTitle());
            existing.setReleaseDate(body.getReleaseDate());
            existing.setType(body.getType());
            existing.setGenre(body.getGenre());
            existing.setDescription(body.getDescription());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
