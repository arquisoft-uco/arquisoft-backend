package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarCompanerosProyectoGradoInteractor
        extends Interactor<ConsultarCompanerosProyectoGradoQuery, List<CompaneroProyectoGradoReadModel>> {}
