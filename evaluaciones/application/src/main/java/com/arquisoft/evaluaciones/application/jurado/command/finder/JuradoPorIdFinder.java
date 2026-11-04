package com.arquisoft.evaluaciones.application.jurado.command.finder;

import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface JuradoPorIdFinder extends Finder<UUID, JuradoDomain> {
}
