package com.arquisoft.solicitudes.application.usuario.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity.UsuarioEntity;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioPorIdFinder extends Finder<UUID, Optional<UsuarioEntity>> {
}
