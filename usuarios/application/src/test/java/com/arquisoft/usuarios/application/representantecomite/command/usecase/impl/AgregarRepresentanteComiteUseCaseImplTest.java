package com.arquisoft.usuarios.application.representantecomite.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.usuarios.application.representantecomite.command.validator.AgregarRepresentanteComiteValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.event.RepresentanteComiteAgregadoEvent;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

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
class AgregarRepresentanteComiteUseCaseImplTest {

    @Mock
    private RepresentanteComiteOutputPort representanteComiteOutputPort;
    @Mock
    private RepresentanteComitePorUsuarioFinder representanteComitePorUsuarioFinder;
    @Mock
    private AgregarRepresentanteComiteValidator agregarRepresentanteComiteValidator;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private AgregarRepresentanteComiteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarRepresentanteComiteUseCaseImpl(
                representanteComiteOutputPort, representanteComitePorUsuarioFinder,
                agregarRepresentanteComiteValidator, eventPublisher, logger);
    }

    private UsuarioDomain usuario() {
        return UsuarioDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeGuardarYPublicarEnOrden_cuandoUsuarioNoEsRepresentante() {
        // Arrange
        var usuario = usuario();
        when(representanteComitePorUsuarioFinder.obtener(usuario.getId()))
                .thenReturn(RepresentanteComiteDomain.VACIO);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(representanteComitePorUsuarioFinder, agregarRepresentanteComiteValidator,
                representanteComiteOutputPort, eventPublisher);
        orden.verify(representanteComitePorUsuarioFinder).obtener(usuario.getId());
        orden.verify(agregarRepresentanteComiteValidator).validar(usuario.getId(), RepresentanteComiteDomain.VACIO);
        orden.verify(representanteComiteOutputPort).guardar(any());
        orden.verify(eventPublisher).publish(any());

        var captorEntity = ArgumentCaptor.forClass(RepresentanteComiteEntity.class);
        verify(representanteComiteOutputPort, times(1)).guardar(captorEntity.capture());
        assertThat(captorEntity.getValue().usuario()).isEqualTo(usuario.getId());
        assertThat(captorEntity.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);

        var captorEvento = ArgumentCaptor.forClass(RepresentanteComiteAgregadoEvent.class);
        verify(eventPublisher, times(1)).publish(captorEvento.capture());
        assertThat(captorEvento.getValue().getUsuario()).isEqualTo(usuario.getId());
        assertThat(captorEvento.getValue().getIdentificador()).isEqualTo(usuario.getIdentificador());
        assertThat(captorEvento.getValue().getNombre()).isEqualTo(usuario.getNombre());
        assertThat(captorEvento.getValue().getEmail()).isEqualTo(usuario.getEmail());

        verify(representanteComitePorUsuarioFinder, times(1)).obtener(usuario.getId());
        verify(representanteComiteOutputPort, never()).reactivar(any());
    }

    @Test
    void debeReactivarYPublicarSinGuardar_cuandoElRepresentanteEstaEliminado() {
        // Arrange
        var usuario = usuario();
        var eliminado = RepresentanteComiteDomain.reconstruir(usuario.getId(), Instant.parse("2026-09-24T10:00:00Z"));
        when(representanteComitePorUsuarioFinder.obtener(usuario.getId())).thenReturn(eliminado);

        // Act
        useCase.ejecutar(usuario);

        // Assert
        var orden = inOrder(agregarRepresentanteComiteValidator, representanteComiteOutputPort, eventPublisher);
        orden.verify(agregarRepresentanteComiteValidator).validar(usuario.getId(), eliminado);
        orden.verify(representanteComiteOutputPort).reactivar(usuario.getId());
        orden.verify(eventPublisher).publish(any(RepresentanteComiteAgregadoEvent.class));
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComitePorUsuarioFinder, times(1)).obtener(usuario.getId());
        assertThat(eliminado.estaEliminado()).isFalse();
    }

    @Test
    void debePropagarYNoEscribirNiPublicar_cuandoElUsuarioYaEsRepresentanteVigente() {
        // Arrange
        var usuario = usuario();
        var vigente = RepresentanteComiteDomain.crear(usuario.getId());
        when(representanteComitePorUsuarioFinder.obtener(usuario.getId())).thenReturn(vigente);
        doThrow(new RepresentanteComiteUsuarioDuplicadoException(usuario.getId()))
                .when(agregarRepresentanteComiteValidator).validar(usuario.getId(), vigente);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(usuario))
                .isInstanceOf(RepresentanteComiteUsuarioDuplicadoException.class);
        verify(representanteComiteOutputPort, never()).guardar(any());
        verify(representanteComiteOutputPort, never()).reactivar(any());
        verify(eventPublisher, never()).publish(any());
    }
}
