package com.arquisoft.artefactos.application.versionartefacto.command.validator;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;

public interface PurgarVersionesProyectoGradoValidator {

    void validar(ExistenciaProyectoGrado existencia, EstadoProyectoGrado estado);
}
