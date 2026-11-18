package com.arquisoft.proyectos.application.proyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.CoordinadorProyectoGradoReadModel;

import java.util.List;

public interface CoordinadorProyectoGradoQueryOutputPort {

    List<CoordinadorProyectoGradoReadModel> consultarPorEstudiante(
            CoordinadorProyectoGradoEstudianteCriteria criteria);
}
