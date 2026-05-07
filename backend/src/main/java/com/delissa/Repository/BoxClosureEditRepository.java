package com.delissa.repository;

import com.delissa.model.BoxClosureEdit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxClosureEditRepository extends JpaRepository<BoxClosureEdit, Long> {
    List<BoxClosureEdit> findByClosure_IdOrderByEditedAtDesc(Long closureId);
}
