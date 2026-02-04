package com.theduckers.backend.service;

import org.springframework.stereotype.Service;

import com.theduckers.backend.entity.Level;
import com.theduckers.backend.repository.LevelRepository;

@Service
public class LevelService {

    private final LevelRepository levelRepository;

    public LevelService(LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    public Level getLevelForTotalPoints(Long totalPoints) {
        return levelRepository
                .findTopByMinPointsLessThanEqualOrderByMinPointsDesc(totalPoints)
                .orElseThrow(() -> new IllegalStateException("No level configuration found"));
    }
}


