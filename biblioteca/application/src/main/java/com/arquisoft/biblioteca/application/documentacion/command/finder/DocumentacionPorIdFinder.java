package com.arquisoft.biblioteca.application.documentacion.command.finder;

import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface DocumentacionPorIdFinder extends Finder<UUID, DocumentacionDomain> {
}
