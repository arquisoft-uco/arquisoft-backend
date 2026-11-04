package com.arquisoft.proyectos.application.proyectogrado.query.usecase;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarProyectosGradoAsesorUseCase
        extends UseCase<ProyectoGradoAsesorCriteria, PaginatedResult<ProyectoGradoReadModel>> {}
