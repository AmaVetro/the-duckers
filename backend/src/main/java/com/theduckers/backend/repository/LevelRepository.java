package com.theduckers.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.theduckers.backend.entity.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {

    Optional<Level> findTopByMinPointsLessThanEqualOrderByMinPointsDesc(Long points);
}
