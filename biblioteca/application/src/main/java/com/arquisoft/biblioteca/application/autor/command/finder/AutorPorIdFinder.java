package com.arquisoft.biblioteca.application.autor.command.finder;

import com.arquisoft.biblioteca.domain.autor.AutorDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface AutorPorIdFinder extends Finder<UUID, AutorDomain> {
}
