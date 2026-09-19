package com.arquisoft.solicitudes.application.destinatario.command.finder;

import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface DestinatarioDeUsuarioFinder extends Finder<UUID, Optional<UUID>> {
}
