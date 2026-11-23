package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ItemsCualitativosCalificadosJuradoFinder extends Finder<UUID, List<ItemCualitativoCalificado>> {
}
