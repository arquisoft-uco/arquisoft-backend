package com.arquisoft.solicitudes.application.respuesta.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;

import java.util.Optional;
import java.util.UUID;

public interface DatosRespuestaFinder extends Finder<UUID, Optional<ResumenRespuesta>> {
}
