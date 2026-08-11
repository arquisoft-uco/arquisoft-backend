package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoInicialFichaPerfilUseCaseTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private AsignarEstadoInicialFichaPerfilValidator asignarEstadoInicialFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private AsignarEstadoInicialFichaPerfilUseCaseImpl useCase;

    @Test
    void debeGuardarElEstadoInicial_cuandoLaFichaExiste() {
        // Arrange
        EstadoFichaPerfilDomain estadoInicial = EstadoFichaPerfilDomain.crear(UUID.randomUUID());

        // Act
        useCase.ejecutar(estadoInicial);

        // Assert
        verify(estadoFichaPerfilOutputPort, times(1)).registrarEstadoInicial(estadoInicial);
    }

    @Test
    void debeValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        EstadoFichaPerfilDomain estadoInicial = EstadoFichaPerfilDomain.crear(fichaPerfil);

        // Act
        useCase.ejecutar(estadoInicial);

        // Assert
        InOrder inOrder = inOrder(asignarEstadoInicialFichaPerfilValidator, estadoFichaPerfilOutputPort);
        inOrder.verify(asignarEstadoInicialFichaPerfilValidator).validar(fichaPerfil);
        inOrder.verify(estadoFichaPerfilOutputPort).registrarEstadoInicial(estadoInicial);
    }

    @Test
    void debePropagarFichaNoEncontrada_cuandoElValidadorFalla() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        EstadoFichaPerfilDomain estadoInicial = EstadoFichaPerfilDomain.crear(fichaPerfil);

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfil))
                .when(asignarEstadoInicialFichaPerfilValidator).validar(fichaPerfil);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(estadoInicial))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
        verify(estadoFichaPerfilOutputPort, never()).registrarEstadoInicial(any());
    }
}
