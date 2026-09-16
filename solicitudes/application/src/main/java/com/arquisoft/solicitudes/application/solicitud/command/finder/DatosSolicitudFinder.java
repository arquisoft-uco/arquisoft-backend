package com.arquisoft.solicitudes.application.solicitud.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;

import java.util.Optional;
import java.util.UUID;

public interface DatosSolicitudFinder extends Finder<UUID, Optional<ResumenSolicitud>> {
}
