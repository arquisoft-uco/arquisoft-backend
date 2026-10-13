package com.arquisoft.usuarios.application.administrador.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;

import java.util.UUID;

public interface AdministradorPorUsuarioFinder extends Finder<UUID, AdministradorDomain> {
}
