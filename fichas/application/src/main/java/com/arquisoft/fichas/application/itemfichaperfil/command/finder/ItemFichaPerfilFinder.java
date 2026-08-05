package com.arquisoft.fichas.application.itemfichaperfil.command.finder;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
import com.arquisoft.shared.rules.Finder;

import java.util.UUID;

public interface ItemFichaPerfilFinder extends Finder<UUID, ItemFichaPerfilDomain> {
}
