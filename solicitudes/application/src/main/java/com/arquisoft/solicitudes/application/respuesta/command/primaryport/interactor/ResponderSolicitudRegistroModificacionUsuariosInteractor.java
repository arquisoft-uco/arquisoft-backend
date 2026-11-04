package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudRegistroModificacionUsuariosCommand;

import java.util.UUID;

public interface ResponderSolicitudRegistroModificacionUsuariosInteractor
        extends Interactor<ResponderSolicitudRegistroModificacionUsuariosCommand, UUID> {}
