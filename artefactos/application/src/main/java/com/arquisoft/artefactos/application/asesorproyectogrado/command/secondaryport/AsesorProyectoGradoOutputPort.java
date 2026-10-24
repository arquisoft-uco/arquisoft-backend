package com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoOutputPort {

    Optional<AsesorProyectoGradoEntity> obtenerPorAsesorYProyectoGrado(UUID asesor, UUID proyectoGrado);

    void guardar(AsesorProyectoGradoEntity vinculo);
}
