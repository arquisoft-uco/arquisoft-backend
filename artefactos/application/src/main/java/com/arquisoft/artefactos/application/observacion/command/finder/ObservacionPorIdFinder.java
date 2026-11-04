package com.arquisoft.artefactos.application.observacion.command.finder;

import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface ObservacionPorIdFinder extends Finder<UUID, ObservacionDomain> {
}
