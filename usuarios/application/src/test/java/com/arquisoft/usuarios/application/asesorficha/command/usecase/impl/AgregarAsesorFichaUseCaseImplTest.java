package com.arquisoft.usuarios.application.asesorficha.command.usecase.impl;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.application.asesorficha.command.validator.AgregarAsesorFichaValidator;
import com.arquisoft.usuarios.domain.asesorficha.event.AsesorFichaAgregadoEvent;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarAsesorFichaUseCaseImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;
    @Mock
    private AsesorFichaPorUsuarioFinder asesorFichaPorUsuarioFinder;
    @Mock
    private AgregarAsesorFichaValidator agregarAsesorFichaValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarAsesorFichaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAsesorFichaUseCaseImpl(
                asesorFichaOutputPort, asesorFichaPorUsuarioFinder, agregarAsesorFichaValidator,
                eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO);
    }

    @Test
    void debeGuardarYPublicar_cuandoUsuarioNoEsAsesorFicha() {
        // Arrange
        var usuario = usuario();
        when(asesorFichaPorUsuarioFinder.obtener(usuario.getId())).thenReturn(AsesorFichaDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var captorEntity = ArgumentCaptor.forClass(AsesorFichaEntity.class);
        verify(asesorFichaOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());

        var captorEvento = ArgumentCaptor.forClass(AsesorFichaAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(asesorFichaPorUsuarioFinder, times(1)).obtener(usuario.getId());
    }

    @Test
    void debeInvocarFinderAntesDelValidator_cuandoAgrega() {
        // Arrange
        var usuario = usuario();
        when(asesorFichaPorUsuarioFinder.obtener(any())).thenReturn(AsesorFichaDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        InOrder orden = inOrder(asesorFichaPorUsuarioFinder, agregarAsesorFichaValidator,
                asesorFichaOutputPort, eventPublisher);
        orden.verify(asesorFichaPorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarAsesorFichaValidator).validar(usuario.getId(), AsesorFichaDomain.VACIO);
        orden.verify(asesorFichaOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());
    }

    @Test
    void debePropagarExcepcion_cuandoValidatorLanza() {
        // Arrange
        var usuario = usuario();
        when(asesorFichaPorUsuarioFinder.obtener(any())).thenReturn(AsesorFichaDomain.crear(usuario.getId()));
        doThrow(new AsesorFichaUsuarioDuplicadoException(usuario.getId()))
                .when(agregarAsesorFichaValidator).validar(any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(AsesorFichaUsuarioDuplicadoException.class);
        verify(asesorFichaOutputPort, never()).guardar(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoElAsesorFichaEstaEliminado() {
        // Arrange
        var usuario = usuario();
        var eliminado = AsesorFichaDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-24T10:00:00Z"));
        when(asesorFichaPorUsuarioFinder.obtener(usuario.getId())).thenReturn(eliminado);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(agregarAsesorFichaValidator, asesorFichaOutputPort, eventPublisher);
        orden.verify(agregarAsesorFichaValidator).validar(usuario.getId(), eliminado);
        orden.verify(asesorFichaOutputPort).reactivar(usuario.getId());
        orden.verify(eventPublisher).publish(any(AsesorFichaAgregadoEvent.class));
        verify(asesorFichaOutputPort, never()).guardar(any());
        verify(asesorFichaPorUsuarioFinder, times(1)).obtener(usuario.getId());
        assertThat(eliminado.estaEliminado()).isFalse();
    }
}
