package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsesorFichaCommandRepository extends JpaRepository<AsesorFichaEntity, UUID> {
}
