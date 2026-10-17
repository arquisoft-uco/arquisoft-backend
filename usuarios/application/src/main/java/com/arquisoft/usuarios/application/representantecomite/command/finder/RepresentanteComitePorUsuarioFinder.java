package com.arquisoft.usuarios.application.representantecomite.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;

import java.util.UUID;

public interface RepresentanteComitePorUsuarioFinder extends Finder<UUID, RepresentanteComiteDomain> {
}
