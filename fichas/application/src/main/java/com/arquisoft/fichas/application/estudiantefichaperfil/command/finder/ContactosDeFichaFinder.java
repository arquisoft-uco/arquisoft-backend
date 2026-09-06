package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ContactosDeFichaFinder extends Finder<UUID, List<ContactoEstudiante>> {
}
