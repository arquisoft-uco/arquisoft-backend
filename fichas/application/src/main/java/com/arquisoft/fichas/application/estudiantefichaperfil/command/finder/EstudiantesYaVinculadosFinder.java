package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.shared.rules.Finder;

import java.util.List;
import java.util.UUID;

public interface EstudiantesYaVinculadosFinder extends Finder<List<EstudianteFichaPerfilDomain>, List<UUID>> {
}
