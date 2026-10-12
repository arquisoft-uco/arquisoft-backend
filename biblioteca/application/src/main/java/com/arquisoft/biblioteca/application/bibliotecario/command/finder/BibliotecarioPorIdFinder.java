package com.arquisoft.biblioteca.application.bibliotecario.command.finder;

import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.shared.finder.Finder;

import java.util.UUID;

public interface BibliotecarioPorIdFinder extends Finder<UUID, BibliotecarioDomain> {
}
