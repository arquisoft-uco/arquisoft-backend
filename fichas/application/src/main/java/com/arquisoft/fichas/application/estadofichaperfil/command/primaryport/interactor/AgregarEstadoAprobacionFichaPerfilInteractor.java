package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoAprobacionFichaPerfilCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface AgregarEstadoAprobacionFichaPerfilInteractor
        extends Interactor<AgregarEstadoAprobacionFichaPerfilCommand, UUID> {}
