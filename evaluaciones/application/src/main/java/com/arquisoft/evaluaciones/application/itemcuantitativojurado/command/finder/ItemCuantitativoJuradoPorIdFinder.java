package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.Optional;
import java.util.UUID;

public interface ItemCuantitativoJuradoPorIdFinder extends Finder<UUID, Optional<ItemCuantitativoJuradoDomain>> {
}
