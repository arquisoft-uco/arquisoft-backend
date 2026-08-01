package com.arquisoft.fichas.application.evaluacionfichaperfil.command;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in.RegistrarEvaluacionFichaPerfilUseCase;
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
public class RegistrarEvaluacionFichaPerfilInteractorImpl implements RegistrarEvaluacionFichaPerfilInteractor {

    private final RegistrarEvaluacionFichaPerfilUseCase registrarEvaluacionFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand command) {
        return registrarEvaluacionFichaPerfilUseCase.ejecutar(command);
    }
}
