package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoInicialFichaPerfilUseCaseTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private AsignarEstadoInicialFichaPerfilValidator asignarEstadoInicialFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AsignarEstadoInicialFichaPerfilUseCaseImpl asignarEstadoInicialFichaPerfilUseCase;

    private final EstadoFichaPerfilDomain estadoInicial =
            EstadoFichaPerfilDomain.crear(UUID.randomUUID());

    @Test
    void debeRegistrarElEstadoInicial_cuandoLaFichaExiste() {
        // Arrange
        stubFichaExiste(true);

        // Act
        asignarEstadoInicialFichaPerfilUseCase.ejecutar(estadoInicial);

        // Assert
        verify(estadoFichaPerfilOutputPort, times(1))
                .registrarEstadoInicial(entidadDe(estadoInicial));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        stubFichaExiste(true);

        // Act
        asignarEstadoInicialFichaPerfilUseCase.ejecutar(estadoInicial);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteFinder, asignarEstadoInicialFichaPerfilValidator,
                estadoFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilExisteFinder).obtener(estadoInicial.getFichaPerfil());
        inOrder.verify(asignarEstadoInicialFichaPerfilValidator)
                .validar(estadoInicial.getFichaPerfil(), true);
        inOrder.verify(estadoFichaPerfilOutputPort).registrarEstadoInicial(entidadDe(estadoInicial));
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        stubFichaExiste(false);
        doThrow(new FichaPerfilNoEncontradaException(estadoInicial.getFichaPerfil()))
                .when(asignarEstadoInicialFichaPerfilValidator)
                .validar(estadoInicial.getFichaPerfil(), false);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstadoInicialFichaPerfilUseCase.ejecutar(estadoInicial))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(estadoFichaPerfilOutputPort, never()).registrarEstadoInicial(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        stubFichaExiste(true);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estadoFichaPerfilOutputPort)
                .registrarEstadoInicial(entidadDe(estadoInicial));

        // Act & Assert
        assertThatThrownBy(() -> asignarEstadoInicialFichaPerfilUseCase.ejecutar(estadoInicial))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubFichaExiste(boolean existe) {
        when(fichaPerfilExisteFinder.obtener(estadoInicial.getFichaPerfil())).thenReturn(existe);
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static EstadoFichaPerfilEntity entidadDe(EstadoFichaPerfilDomain estado) {
        return argThat(entity -> entity.id().equals(estado.getId())
                && entity.fichaPerfilId().equals(estado.getFichaPerfil())
                && entity.estadoFicha().equals(estado.getEstadoFicha().getId()));
    }
}
