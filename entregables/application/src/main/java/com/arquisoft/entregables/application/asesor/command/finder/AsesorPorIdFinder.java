package com.arquisoft.entregables.application.asesor.command.finder;

import com.arquisoft.entregables.domain.asesor.AsesorDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface AsesorPorIdFinder extends Finder<UUID, AsesorDomain> {
}
