package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.entity.RepresentanteComiteCurriculumJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepresentanteComiteCurriculumCommandRepository extends JpaRepository<RepresentanteComiteCurriculumJpaEntity, UUID> {

    Boolean existsByUsuario(UUID usuario);
}
