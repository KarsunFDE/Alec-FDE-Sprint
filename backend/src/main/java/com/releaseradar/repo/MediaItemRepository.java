package com.releaseradar.repo;

import com.releaseradar.model.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {

    @Query(value =
        "SELECT * FROM media_item " +
        "WHERE LOWER(title) LIKE LOWER(CONCAT('%', :q, '%')) " +
        "ORDER BY release_date " +
        "LIMIT :size OFFSET :offset",
        nativeQuery = true)
    List<MediaItem> search(@Param("q") String q,
                           @Param("size") int size,
                           @Param("offset") int offset);
}
