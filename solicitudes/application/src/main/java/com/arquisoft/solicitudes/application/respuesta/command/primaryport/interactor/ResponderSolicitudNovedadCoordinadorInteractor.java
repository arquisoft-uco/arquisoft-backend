package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;

import java.util.UUID;

public interface ResponderSolicitudNovedadCoordinadorInteractor
        extends Interactor<ResponderSolicitudNovedadCoordinadorCommand, UUID> {}
