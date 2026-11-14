package com.arquisoft.biblioteca.application.autor.command.usecase;

import com.arquisoft.biblioteca.application.autor.command.result.AgregacionAutorResult;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarAutorUseCase extends UseCase<AutorDomain, AgregacionAutorResult> {
}
