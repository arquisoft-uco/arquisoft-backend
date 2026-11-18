package com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;

import java.util.List;

public interface AsesorActualProyectoGradoQueryOutputPort {

    List<AsesorActualProyectoGradoReadModel> consultarPorEstudiante(
            AsesorActualProyectoGradoEstudianteCriteria criteria);
}
