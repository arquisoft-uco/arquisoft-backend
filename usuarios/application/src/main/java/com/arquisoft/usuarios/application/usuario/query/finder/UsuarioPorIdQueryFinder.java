package com.arquisoft.usuarios.application.usuario.query.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface UsuarioPorIdQueryFinder extends Finder<UUID, UsuarioDomain> {
}
