package com.arquisoft.mapas_ruta.application.maparuta.command.finder;

import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface MapaRutaPorIdFinder extends Finder<UUID, MapaRutaDomain> {
}
