package com.arquisoft.fichas.application.representantecomite.command.finder;

import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface RepresentanteComitePorIdFinder extends Finder<UUID, RepresentanteComiteDomain> {
}
