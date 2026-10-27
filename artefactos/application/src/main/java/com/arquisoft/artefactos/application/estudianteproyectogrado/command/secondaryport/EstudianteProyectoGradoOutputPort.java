package com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.PertenenciaProyectoGradoEntity;

import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoOutputPort {

    List<PertenenciaProyectoGradoEntity> obtenerPertenenciasVigentes(List<UUID> artefactos, List<UUID> estudiantes);
}
