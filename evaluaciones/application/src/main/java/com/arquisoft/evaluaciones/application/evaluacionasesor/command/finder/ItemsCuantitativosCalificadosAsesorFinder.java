package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ItemsCuantitativosCalificadosAsesorFinder extends Finder<UUID, List<ItemCuantitativoCalificado>> {
}
