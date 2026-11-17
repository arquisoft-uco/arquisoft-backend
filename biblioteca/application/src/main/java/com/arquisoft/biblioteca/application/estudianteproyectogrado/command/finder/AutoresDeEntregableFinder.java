package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder;

import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface AutoresDeEntregableFinder extends Finder<UUID, List<AutorDeEntregable>> {
}
