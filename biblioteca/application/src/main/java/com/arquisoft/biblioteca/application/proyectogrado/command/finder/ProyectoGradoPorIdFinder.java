package com.arquisoft.biblioteca.application.proyectogrado.command.finder;

import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface ProyectoGradoPorIdFinder extends Finder<UUID, ProyectoGradoDomain> {
}
