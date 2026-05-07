package com.delissa.repository;

import com.delissa.model.BoxClosure;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxClosureRepository extends JpaRepository<BoxClosure, Long> {
    List<BoxClosure> findByBusinessDateOrderByClosedAtDesc(LocalDate businessDate);

    Optional<BoxClosure> findTopByOrderByClosedAtDesc();

    boolean existsByBusinessDate(LocalDate businessDate);
}
