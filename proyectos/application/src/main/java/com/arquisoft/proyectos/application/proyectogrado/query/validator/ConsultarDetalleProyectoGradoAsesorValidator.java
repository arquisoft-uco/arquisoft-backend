package com.arquisoft.proyectos.application.proyectogrado.query.validator;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;

public interface ConsultarDetalleProyectoGradoAsesorValidator {

    void validar(DetalleProyectoGradoAsesorCriteria criteria, boolean proyectoExiste, boolean asesorAsignado);
}
