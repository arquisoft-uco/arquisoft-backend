package com.arquisoft.proyectos.application.estadoproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;

import java.util.UUID;

public interface ConsultarHistorialEstadosProyectoGradoValidator {

    void validar(HistorialEstadosProyectoGradoCriteria criteria, UUID coordinadorDelProyecto, boolean asesorAsignado);
}
