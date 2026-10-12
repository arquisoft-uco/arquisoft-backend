package com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoOutputPort {

    void registrar(AsesorProyectoGradoEntity vinculo);

    boolean existePorAsesorYProyectoGradoConEstadoVigente(UUID asesor, UUID proyectoGrado, String estadoAsesor);

    long contarPorProyectoGradoConEstadoVigente(UUID proyectoGrado, String estadoAsesor);

    Optional<AsesorProyectoGradoEntity> obtenerVigentePorAsesorYProyectoGrado(UUID asesor, UUID proyectoGrado);
}
