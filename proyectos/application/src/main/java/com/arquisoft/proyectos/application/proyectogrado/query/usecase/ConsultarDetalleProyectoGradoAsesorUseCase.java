package com.arquisoft.proyectos.application.proyectogrado.query.usecase;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarDetalleProyectoGradoAsesorUseCase
        extends UseCase<DetalleProyectoGradoAsesorCriteria, ProyectoGradoReadModel> {}
