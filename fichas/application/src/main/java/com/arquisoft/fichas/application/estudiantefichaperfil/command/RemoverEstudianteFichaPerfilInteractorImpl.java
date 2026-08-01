package com.arquisoft.fichas.application.estudiantefichaperfil.command;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Punto de entrada de la operación: delimita la transacción y orquesta el caso de uso.
 *
 * <p>La transacción vive en el interactor y no en el caso de uso porque una operación
 * puede apalancarse en varios casos de uso; declararla aquí garantiza una única unidad
 * de trabajo para toda la operación. Además asegura que el
 * {@code fichasTransactionManager} esté activo cuando se publiquen eventos de dominio,
 * requisito del repositorio de outbox ({@code ContextAwareEventPublicationRepository}).</p>
 */
@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilInteractorImpl implements RemoverEstudianteFichaPerfilInteractor {

    private final RemoverEstudianteFichaPerfilUseCase removerEstudianteFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverEstudianteFichaPerfilCommand command) {
        removerEstudianteFichaPerfilUseCase.ejecutar(command);
    }
}
