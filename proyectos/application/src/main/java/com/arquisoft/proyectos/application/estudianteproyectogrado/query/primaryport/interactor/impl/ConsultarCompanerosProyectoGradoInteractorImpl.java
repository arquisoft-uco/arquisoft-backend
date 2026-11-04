package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.ConsultarCompanerosProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper.ConsultarCompanerosProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.usecase.ConsultarCompanerosProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCompanerosProyectoGradoInteractorImpl implements ConsultarCompanerosProyectoGradoInteractor {

    private final ConsultarCompanerosProyectoGradoUseCase consultarCompanerosProyectoGradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<CompaneroProyectoGradoReadModel> ejecutar(ConsultarCompanerosProyectoGradoQuery entrada) {
        var criteria = ConsultarCompanerosProyectoGradoMapper.toCriteria(entrada);
        return consultarCompanerosProyectoGradoUseCase.ejecutar(criteria);
    }
}
