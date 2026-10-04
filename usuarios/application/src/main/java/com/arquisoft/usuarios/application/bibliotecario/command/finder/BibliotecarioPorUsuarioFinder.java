package com.arquisoft.usuarios.application.bibliotecario.command.finder;

import com.arquisoft.shared.finder.Finder;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;

import java.util.UUID;

public interface BibliotecarioPorUsuarioFinder extends Finder<UUID, BibliotecarioDomain> {
}
