package com.theduckers.backend.repository;

import com.theduckers.backend.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
}
