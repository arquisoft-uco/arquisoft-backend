package com.arquisoft.usuarios.application.jurado.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;

import java.util.UUID;

public interface JuradoPorUsuarioFinder extends Finder<UUID, JuradoDomain> {
}
