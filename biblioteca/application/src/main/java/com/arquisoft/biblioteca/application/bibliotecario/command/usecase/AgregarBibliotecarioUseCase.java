package com.arquisoft.biblioteca.application.bibliotecario.command.usecase;

import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarBibliotecarioUseCase extends UseCase<BibliotecarioDomain, AgregacionBibliotecarioResult> {
}
