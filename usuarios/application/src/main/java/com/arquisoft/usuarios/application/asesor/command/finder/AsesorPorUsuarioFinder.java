package com.arquisoft.usuarios.application.asesor.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;

import java.util.UUID;

public interface AsesorPorUsuarioFinder extends Finder<UUID, AsesorDomain> {
}
