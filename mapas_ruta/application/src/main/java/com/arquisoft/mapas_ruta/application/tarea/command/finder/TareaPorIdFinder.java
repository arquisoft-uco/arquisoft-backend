package com.arquisoft.mapas_ruta.application.tarea.command.finder;

import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface TareaPorIdFinder extends Finder<UUID, TareaDomain> {
}
