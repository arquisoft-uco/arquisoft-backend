package com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProyectoGradoCommandRepository extends JpaRepository<ProyectoGradoJpaEntity, UUID> {

    boolean existsByFichaPerfilId(UUID fichaPerfilId);
}
