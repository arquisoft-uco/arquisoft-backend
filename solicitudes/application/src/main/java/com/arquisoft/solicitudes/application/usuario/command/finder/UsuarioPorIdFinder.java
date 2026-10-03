package com.arquisoft.solicitudes.application.usuario.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface UsuarioPorIdFinder extends Finder<UUID, UsuarioDomain> {
}
