package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ResolverIdentidadSesionCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ResolverIdentidadSesionInteractor
        extends Interactor<ResolverIdentidadSesionCommand, ResolucionSesionResult> {}
