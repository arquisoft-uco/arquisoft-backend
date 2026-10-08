package com.arquisoft.solicitudes.application.remitente.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.application.remitente.command.validator.RegistrarRemitenteValidator;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarRemitenteUseCaseImplTest {

    @Mock private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock private RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    @Mock private RegistrarRemitenteValidator validator;
    @Mock private RemitenteOutputPort remitenteOutputPort;
    @Mock private AppLogger logger;

    private RegistrarRemitenteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarRemitenteUseCaseImpl(
                usuarioPorIdFinder, remitenteDeUsuarioFinder, validator, remitenteOutputPort, logger);
    }

    private static UsuarioDomain replica(UUID id) {
        return UsuarioDomain.reconstruir(id, "ID-1", "Nombre", "nombre@uco.edu.co", Instant.now());
    }

    @Test
    void debeNoRegistrar_cuandoYaExisteFilaParaElUsuario() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        var usuario = replica(remitente.getUsuario());
        when(usuarioPorIdFinder.obtener(remitente.getUsuario())).thenReturn(usuario);
        when(remitenteDeUsuarioFinder.obtener(remitente.getUsuario())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(remitente);

        // Assert
        verify(validator).validar(remitente, usuario);
        verify(remitenteOutputPort, never()).registrar(any());
    }

    @Test
    void debeRegistrarElCandidato_cuandoNoExisteFilaPrevia() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        when(usuarioPorIdFinder.obtener(remitente.getUsuario())).thenReturn(replica(remitente.getUsuario()));
        when(remitenteDeUsuarioFinder.obtener(remitente.getUsuario())).thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        useCase.ejecutar(remitente);

        // Assert
        var captor = ArgumentCaptor.forClass(RemitenteEntity.class);
        verify(remitenteOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(remitente.getId());
        assertThat(captor.getValue().usuario()).isEqualTo(remitente.getUsuario());
    }

    @Test
    void debeLanzarYNoTocarLaEscritura_cuandoElUsuarioNoExiste() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        when(usuarioPorIdFinder.obtener(remitente.getUsuario())).thenReturn(UsuarioDomain.VACIO);
        doThrow(new RemitenteNoEncontradoException(remitente.getUsuario()))
                .when(validator).validar(remitente, UsuarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remitente))
                .isInstanceOf(RemitenteNoEncontradoException.class);

        verifyNoInteractions(remitenteDeUsuarioFinder, remitenteOutputPort);
    }

    @Test
    void debeValidarAntesDeConsultarLaFilaYRegistrar_cuandoElUsuarioExiste() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        when(usuarioPorIdFinder.obtener(remitente.getUsuario())).thenReturn(replica(remitente.getUsuario()));
        when(remitenteDeUsuarioFinder.obtener(remitente.getUsuario())).thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        useCase.ejecutar(remitente);

        // Assert
        var inOrder = inOrder(usuarioPorIdFinder, validator, remitenteDeUsuarioFinder, remitenteOutputPort);
        inOrder.verify(usuarioPorIdFinder).obtener(remitente.getUsuario());
        inOrder.verify(validator).validar(any(), any());
        inOrder.verify(remitenteDeUsuarioFinder).obtener(remitente.getUsuario());
        inOrder.verify(remitenteOutputPort).registrar(any());
    }
}
