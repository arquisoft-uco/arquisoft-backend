package com.arquisoft.solicitudes.application.solicitud.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface DatosUsuarioFinder extends Finder<UUID, UsuarioDomain> {
}
