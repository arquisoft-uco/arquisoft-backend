package com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper.AsignarEstudiantesFichaPerfilMapper;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilInteractorImpl implements RegistrarFichaPerfilInteractor {

    private final RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;
    private final AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;
    private final AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        FichaPerfilDomain ficha = FichaPerfilDomain.crear(command.tituloProyecto(), command.asesorFicha());

        UUID fichaPerfil = registrarFichaPerfilUseCase.ejecutar(ficha);

        asignarEstadoInicialFichaPerfilUseCase.ejecutar(EstadoFichaPerfilDomain.crear(fichaPerfil));

        asignarEstudiantesFichaPerfilUseCase.ejecutar(AsignarEstudiantesFichaPerfilMapper.toDomain(
                new AsignarEstudiantesFichaPerfilCommand(fichaPerfil, command.estudiantes())));

        return fichaPerfil;
    }
}
