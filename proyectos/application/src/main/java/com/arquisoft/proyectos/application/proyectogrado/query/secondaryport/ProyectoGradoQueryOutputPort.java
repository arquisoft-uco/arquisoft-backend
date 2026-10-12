package com.arquisoft.proyectos.application.proyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

import java.util.List;

public interface ProyectoGradoQueryOutputPort {

    PaginatedResult<ProyectoGradoReadModel> consultarTodas(ProyectoGradoCriteria criteria);

    List<ProyectoGradoReadModel> consultarPorEstudiante(ProyectoGradoEstudianteCriteria criteria);

    PaginatedResult<ProyectoGradoReadModel> consultarPorAsesor(ProyectoGradoAsesorCriteria criteria);
}
