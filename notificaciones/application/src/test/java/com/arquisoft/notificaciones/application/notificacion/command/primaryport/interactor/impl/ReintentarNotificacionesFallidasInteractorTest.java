package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.impl;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.ReintentarNotificacionesFallidasUseCase;
import com.arquisoft.notificaciones.domain.notificacion.ReintentoNotificacionesDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReintentarNotificacionesFallidasInteractorTest {

    @Mock
    private ReintentarNotificacionesFallidasUseCase reintentarNotificacionesFallidasUseCase;

    @InjectMocks
    private ReintentarNotificacionesFallidasInteractorImpl reintentarNotificacionesFallidasInteractor;

    @Test
    void debeDelegarYDevolverElResultado_cuandoSeEjecuta() {
        // Arrange
        var command = ReintentarNotificacionesFallidasCommand.crear(3, 50);
        var esperado = new ReintentoNotificacionesResult(2, 1, 4);
        when(reintentarNotificacionesFallidasUseCase.ejecutar(dominioEquivalenteA(command)))
                .thenReturn(esperado);

        // Act
        ReintentoNotificacionesResult resultado =
                reintentarNotificacionesFallidasInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(reintentarNotificacionesFallidasUseCase).ejecutar(dominioEquivalenteA(command));
    }

    // El caso de uso recibe el dominio que construyo el mapper: se compara por valor.
    private static ReintentoNotificacionesDomain dominioEquivalenteA(
            ReintentarNotificacionesFallidasCommand command) {
        return argThat(reintento -> reintento.getMaxIntentos() == command.maxIntentos()
                && reintento.getLimite() == command.limite());
    }
}
