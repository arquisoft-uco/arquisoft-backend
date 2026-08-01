package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
public class RegistrarFichaPerfilInteractorImpl implements RegistrarFichaPerfilInteractor {

    private final RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        return registrarFichaPerfilUseCase.ejecutar(command);
    }
}
