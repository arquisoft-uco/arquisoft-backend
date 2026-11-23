package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder;

import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ItemsCuantitativosCalificadosJuradoFinder extends Finder<UUID, List<ItemCuantitativoCalificado>> {
}
