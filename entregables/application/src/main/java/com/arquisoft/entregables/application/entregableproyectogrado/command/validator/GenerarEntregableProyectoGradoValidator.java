package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;

public interface GenerarEntregableProyectoGradoValidator {

    void validar(ExistenciaProyectoGrado proyectoGrado, VigenciaAsesor asesor);
}
