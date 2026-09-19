package com.arquisoft.solicitudes.application.remitente.command.finder;

import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface RemitenteDeUsuarioFinder extends Finder<UUID, Optional<UUID>> {
}
