package com.arquisoft.mapas_ruta.application.proyectogrado.command.finder;

import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface ProyectoGradoPorIdFinder extends Finder<UUID, ProyectoGradoDomain> {
}
