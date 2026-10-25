package com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoOutputPort {

    Optional<AsesorProyectoGradoEntity> obtenerPorAsesorYProyectoGrado(UUID asesor, UUID proyectoGrado);

    Optional<Instant> obtenerUltimoOcurridoEnPorProyectoGrado(UUID proyectoGrado);

    List<AsesorProyectoGradoEntity> obtenerMasRecientesPorProyectoGrado(UUID proyectoGrado);

    void guardar(AsesorProyectoGradoEntity vinculo);
}
