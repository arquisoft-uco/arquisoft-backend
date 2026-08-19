package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.event.AsesorFichaCambiadoEvent;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarAsesorFichaUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private AsesorFichaFinder asesorFichaFinder;

    @Mock
    private EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;

    @Mock
    private CambiarAsesorFichaValidator cambiarAsesorFichaValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;
    @InjectMocks
    private CambiarAsesorFichaUseCaseImpl cambiarAsesorFichaUseCase;

    private final UUID asesorActual = UUID.randomUUID();
    private final UUID nuevoAsesor = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.crear("Título de prueba", asesorActual);
    private final AsesorFichaDomain contacto =
            AsesorFichaDomain.reconstruir(UUID.randomUUID(), "A001", "Ana Asesora", "ana@arquisoft.com");
    private final EstadoFichaPerfilDomain estadoEnConstruccion =
            EstadoFichaPerfilDomain.crear(ficha.getId());

    @Test
    void debeActualizarElAsesor_cuandoDatosValidos() {
        // Arrange
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), nuevoAsesor);
        stubConsultas();

        // Act
        cambiarAsesorFichaUseCase.ejecutar(cambio);

        // Assert
        verify(fichaPerfilOutputPort, times(1)).actualizarAsesor(eq(ficha.getId()), eq(nuevoAsesor));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), nuevoAsesor);
        stubConsultas();

        // Act
        cambiarAsesorFichaUseCase.ejecutar(cambio);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilFinder, asesorFichaFinder, estadoActualFichaPerfilFinder,
                cambiarAsesorFichaValidator, fichaPerfilOutputPort);
        inOrder.verify(fichaPerfilFinder).obtener(ficha.getId());
        inOrder.verify(asesorFichaFinder).obtener(nuevoAsesor);
        inOrder.verify(estadoActualFichaPerfilFinder).obtener(ficha.getId());
        inOrder.verify(cambiarAsesorFichaValidator).validar(
                cambio, ficha, contacto, estadoEnConstruccion);
        inOrder.verify(fichaPerfilOutputPort).actualizarAsesor(eq(ficha.getId()), eq(nuevoAsesor));
    }

    @Test
    void debePublicarElEventoConLosDatosDelNuevoAsesor_cuandoSeCambia() {
        // Arrange
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), nuevoAsesor);
        stubConsultas();

        // Act
        cambiarAsesorFichaUseCase.ejecutar(cambio);

        // Assert
        ArgumentCaptor<AsesorFichaCambiadoEvent> captor =
                ArgumentCaptor.forClass(AsesorFichaCambiadoEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), nuevoAsesor);
        when(fichaPerfilFinder.obtener(ficha.getId())).thenReturn(Optional.empty());
        when(asesorFichaFinder.obtener(nuevoAsesor)).thenReturn(Optional.of(contacto));
        when(estadoActualFichaPerfilFinder.obtener(ficha.getId())).thenReturn(Optional.empty());
        doThrow(new FichaPerfilNoEncontradaException(ficha.getId()))
                .when(cambiarAsesorFichaValidator)
                .validar(cambio, FichaPerfilDomain.VACIO, contacto, EstadoFichaPerfilDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(cambio))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(fichaPerfilOutputPort, never()).actualizarAsesor(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElAsesorEsElMismo() {
        // Arrange
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), asesorActual);
        when(fichaPerfilFinder.obtener(ficha.getId())).thenReturn(Optional.of(ficha));
        when(asesorFichaFinder.obtener(asesorActual)).thenReturn(Optional.of(contacto));
        when(estadoActualFichaPerfilFinder.obtener(ficha.getId()))
                .thenReturn(Optional.of(estadoEnConstruccion));
        doThrow(new MismoAsesorFichaException(asesorActual))
                .when(cambiarAsesorFichaValidator).validar(
                        cambio, ficha, contacto, estadoEnConstruccion);

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(cambio))
                .isInstanceOf(MismoAsesorFichaException.class);

        verify(fichaPerfilOutputPort, never()).actualizarAsesor(any(), any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var cambio = CambioAsesorFichaDomain.crear(ficha.getId(), nuevoAsesor);
        stubConsultas();
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort).actualizarAsesor(eq(ficha.getId()), eq(nuevoAsesor));

        // Act & Assert
        assertThatThrownBy(() -> cambiarAsesorFichaUseCase.ejecutar(cambio))
                .isInstanceOf(InfrastructureException.class);

        verify(eventPublisher, never()).publish(any());
    }

    private void stubConsultas() {
        when(fichaPerfilFinder.obtener(ficha.getId())).thenReturn(Optional.of(ficha));
        when(asesorFichaFinder.obtener(nuevoAsesor)).thenReturn(Optional.of(contacto));
        when(estadoActualFichaPerfilFinder.obtener(ficha.getId()))
                .thenReturn(Optional.of(estadoEnConstruccion));
    }
}
