package com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.PropiedadEvaluacionAsesorEstudiante;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EstudiantePropietarioEvaluacionAsesorRule;

public class EstudiantePropietarioEvaluacionAsesorRuleImpl implements EstudiantePropietarioEvaluacionAsesorRule {

    @Override
    public void validar(PropiedadEvaluacionAsesorEstudiante propiedad) {
        if (!propiedad.estudiantesVinculados().contains(propiedad.estudiante())) {
            throw new EvaluacionAsesorNoPerteneceEstudianteException(
                    propiedad.evaluacionAsesor(), propiedad.estudiante());
        }
    }
}
