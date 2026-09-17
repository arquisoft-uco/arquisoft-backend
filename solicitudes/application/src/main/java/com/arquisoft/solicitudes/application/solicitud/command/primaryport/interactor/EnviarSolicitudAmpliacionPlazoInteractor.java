package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;

import java.util.UUID;

public interface EnviarSolicitudAmpliacionPlazoInteractor
        extends Interactor<EnviarSolicitudAmpliacionPlazoCommand, UUID> {}
