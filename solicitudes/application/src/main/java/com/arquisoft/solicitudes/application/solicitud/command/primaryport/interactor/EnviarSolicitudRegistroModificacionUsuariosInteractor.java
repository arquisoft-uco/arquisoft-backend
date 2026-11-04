package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudRegistroModificacionUsuariosCommand;

import java.util.UUID;

public interface EnviarSolicitudRegistroModificacionUsuariosInteractor
        extends Interactor<EnviarSolicitudRegistroModificacionUsuariosCommand, UUID> {}
