package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface IntegrantesVigentesDeFichaFinder extends Finder<UUID, List<IntegranteFicha>> {
}
