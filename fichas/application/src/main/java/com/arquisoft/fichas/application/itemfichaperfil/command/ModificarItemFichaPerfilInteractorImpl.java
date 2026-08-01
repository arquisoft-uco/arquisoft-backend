package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilUseCase;
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
public class ModificarItemFichaPerfilInteractorImpl implements ModificarItemFichaPerfilInteractor {

    private final ModificarItemFichaPerfilUseCase modificarItemFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarItemFichaPerfilCommand command) {
        modificarItemFichaPerfilUseCase.ejecutar(command);
    }
}
