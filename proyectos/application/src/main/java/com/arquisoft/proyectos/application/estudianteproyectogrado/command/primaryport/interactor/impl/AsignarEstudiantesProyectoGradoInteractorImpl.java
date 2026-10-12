package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.AsignarEstudiantesProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.mapper.AsignarEstudiantesProyectoGradoMapper;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.AsignarEstudiantesProyectoGradoCommand;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.usecase.AsignarEstudiantesProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesProyectoGradoInteractorImpl implements AsignarEstudiantesProyectoGradoInteractor {

    private final AsignarEstudiantesProyectoGradoUseCase asignarEstudiantesProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public void ejecutar(AsignarEstudiantesProyectoGradoCommand command) {
        asignarEstudiantesProyectoGradoUseCase.ejecutar(AsignarEstudiantesProyectoGradoMapper.toDomain(command));
    }
}
