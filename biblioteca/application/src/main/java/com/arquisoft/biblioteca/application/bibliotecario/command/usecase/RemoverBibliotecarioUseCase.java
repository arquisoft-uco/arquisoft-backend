package com.arquisoft.biblioteca.application.bibliotecario.command.usecase;

import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverBibliotecarioUseCase extends UseCase<BibliotecarioDomain, RemocionBibliotecarioResult> {
}
