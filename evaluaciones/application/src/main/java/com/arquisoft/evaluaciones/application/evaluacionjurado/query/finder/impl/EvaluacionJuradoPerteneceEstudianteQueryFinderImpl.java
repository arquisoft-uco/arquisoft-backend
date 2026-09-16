package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.estudiantesproyectogrado.query.secondaryport.EstudiantesProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.EvaluacionJuradoPerteneceEstudianteQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoPerteneceEstudianteQueryFinderImpl
        implements EvaluacionJuradoPerteneceEstudianteQueryFinder {

    private final EvaluacionJuradoAccesoQueryOutputPort evaluacionJuradoAccesoQueryOutputPort;
    private final EstudiantesProyectoGradoOutputPort estudiantesProyectoGradoOutputPort;

    @Override
    public Boolean obtener(EvaluacionCualitativaJuradoCriteria criteria) {
        var proyecto = evaluacionJuradoAccesoQueryOutputPort.obtenerProyecto(criteria.evaluacionJuradoId());
        if (proyecto.isEmpty()) {
            return false;
        }
        var estudiantes = estudiantesProyectoGradoOutputPort.obtenerEstudiantes(proyecto.get());
        return estudiantes.contains(criteria.estudianteId());
    }
}
