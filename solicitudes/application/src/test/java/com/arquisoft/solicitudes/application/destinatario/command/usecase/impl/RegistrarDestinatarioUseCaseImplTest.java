package com.arquisoft.solicitudes.application.destinatario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.application.destinatario.command.validator.RegistrarDestinatarioValidator;
import com.arquisoft.solicitudes.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarDestinatarioUseCaseImplTest {

    @Mock private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock private DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    @Mock private RegistrarDestinatarioValidator validator;
    @Mock private DestinatarioOutputPort destinatarioOutputPort;
    @Mock private AppLogger logger;

    private RegistrarDestinatarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarDestinatarioUseCaseImpl(
                usuarioPorIdFinder, destinatarioDeUsuarioFinder, validator, destinatarioOutputPort, logger);
    }

    private static UsuarioDomain replica(UUID id) {
        return UsuarioDomain.reconstruir(id, "ID-1", "Nombre", "nombre@uco.edu.co", Instant.now());
    }

    @Test
    void debeNoRegistrar_cuandoYaExisteFilaParaElUsuario() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        var usuario = replica(destinatario.getUsuario());
        when(usuarioPorIdFinder.obtener(destinatario.getUsuario())).thenReturn(usuario);
        when(destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(destinatario);

        // Assert
        verify(validator).validar(destinatario, usuario);
        verify(destinatarioOutputPort, never()).registrar(any());
    }

    @Test
    void debeRegistrarElCandidato_cuandoNoExisteFilaPrevia() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        when(usuarioPorIdFinder.obtener(destinatario.getUsuario())).thenReturn(replica(destinatario.getUsuario()));
        when(destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario())).thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        useCase.ejecutar(destinatario);

        // Assert
        var captor = ArgumentCaptor.forClass(DestinatarioEntity.class);
        verify(destinatarioOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(destinatario.getId());
        assertThat(captor.getValue().usuario()).isEqualTo(destinatario.getUsuario());
    }

    @Test
    void debeLanzarYNoTocarLaEscritura_cuandoElUsuarioNoExiste() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        when(usuarioPorIdFinder.obtener(destinatario.getUsuario())).thenReturn(UsuarioDomain.VACIO);
        doThrow(new DestinatarioNoEncontradoException(destinatario.getUsuario()))
                .when(validator).validar(destinatario, UsuarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(destinatario))
                .isInstanceOf(DestinatarioNoEncontradoException.class);

        verifyNoInteractions(destinatarioDeUsuarioFinder, destinatarioOutputPort);
    }

    @Test
    void debeValidarAntesDeConsultarLaFilaYRegistrar_cuandoElUsuarioExiste() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        when(usuarioPorIdFinder.obtener(destinatario.getUsuario())).thenReturn(replica(destinatario.getUsuario()));
        when(destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario())).thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        useCase.ejecutar(destinatario);

        // Assert
        InOrder inOrder = inOrder(usuarioPorIdFinder, validator, destinatarioDeUsuarioFinder, destinatarioOutputPort);
        inOrder.verify(usuarioPorIdFinder).obtener(destinatario.getUsuario());
        inOrder.verify(validator).validar(any(), any());
        inOrder.verify(destinatarioDeUsuarioFinder).obtener(destinatario.getUsuario());
        inOrder.verify(destinatarioOutputPort).registrar(any());
    }
}
