package com.arquisoft.mapas_ruta.application.maparuta.command.finder;

import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.shared.finder.Finder;

import java.time.LocalDate;
import java.util.List;

public interface MapasRutaConPlazoVencidoFinder extends Finder<LocalDate, List<MapaRutaDomain>> {
}
