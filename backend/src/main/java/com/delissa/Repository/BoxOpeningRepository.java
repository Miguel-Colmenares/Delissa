package com.delissa.repository;

import com.delissa.model.BoxOpening;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxOpeningRepository extends JpaRepository<BoxOpening, Long> {
    List<BoxOpening> findByBusinessDateOrderByOpenedAtDesc(LocalDate businessDate);

    Optional<BoxOpening> findTopByBusinessDateOrderByOpenedAtDesc(LocalDate businessDate);
}
