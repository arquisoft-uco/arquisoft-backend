package com.arquisoft.usuarios.application.usuario.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface UsuarioPorIdFinder extends Finder<UUID, UsuarioDomain> {
}
