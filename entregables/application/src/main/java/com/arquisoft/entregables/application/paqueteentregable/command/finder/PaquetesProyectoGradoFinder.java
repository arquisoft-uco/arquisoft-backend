package com.arquisoft.entregables.application.paqueteentregable.command.finder;

import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface PaquetesProyectoGradoFinder extends Finder<UUID, List<PaqueteEntregableDomain>> {
}
