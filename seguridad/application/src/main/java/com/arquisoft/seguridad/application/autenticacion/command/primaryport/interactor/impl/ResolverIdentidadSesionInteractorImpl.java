package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.ResolverIdentidadSesionInteractor;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper.ResolverIdentidadSesionMapper;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ResolverIdentidadSesionCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.usecase.ResolverIdentidadSesionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResolverIdentidadSesionInteractorImpl implements ResolverIdentidadSesionInteractor {

    private final ResolverIdentidadSesionUseCase resolverIdentidadSesionUseCase;

    @Override
    public ResolucionSesionResult ejecutar(ResolverIdentidadSesionCommand entrada) {
        return resolverIdentidadSesionUseCase.ejecutar(ResolverIdentidadSesionMapper.toDomain(entrada));
    }
}
