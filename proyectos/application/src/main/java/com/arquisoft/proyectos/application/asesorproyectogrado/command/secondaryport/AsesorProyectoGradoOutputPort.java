package com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoOutputPort {

    void asignar(AsesorProyectoGradoEntity vinculo);

    boolean existePorAsesorYProyectoGradoYEstado(UUID asesor, UUID proyectoGrado, String estadoAsesor);

    long contarPorProyectoGradoYEstado(UUID proyectoGrado, String estadoAsesor);

    Optional<AsesorProyectoGradoEntity> obtenerPorIdYProyectoGrado(UUID id, UUID proyectoGrado);

    void actualizarEstado(UUID id, String estadoAsesor);
}
