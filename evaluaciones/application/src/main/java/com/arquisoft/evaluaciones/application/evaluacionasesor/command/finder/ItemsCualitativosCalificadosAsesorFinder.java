package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ItemsCualitativosCalificadosAsesorFinder extends Finder<UUID, List<ItemCualitativoCalificado>> {
}
