package com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport;

import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoOutputPort {

    Optional<AsesorProyectoGradoEntity> obtenerPorAsesorYProyectoGrado(UUID asesor, UUID proyectoGrado);

    Optional<UUID> obtenerAsesorVigentePorProyectoGrado(UUID proyectoGrado);

    Optional<UUID> obtenerAsesorVigentePorEntregable(UUID entregable);

    void guardar(AsesorProyectoGradoEntity vinculo);
}
