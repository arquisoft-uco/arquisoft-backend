package com.arquisoft.solicitudes.application.destinatario.command.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.destinatario.command.finder.DestinatarioDeUsuarioFinder;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarDestinatarioUseCaseImplTest {

    @Mock private DestinatarioDeUsuarioFinder destinatarioDeUsuarioFinder;
    @Mock private DestinatarioOutputPort destinatarioOutputPort;

    private RegistrarDestinatarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarDestinatarioUseCaseImpl(destinatarioDeUsuarioFinder, destinatarioOutputPort);
    }

    @Test
    void debeReutilizarLaFilaExistente_cuandoElFinderDevuelveUnId() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        var destinatarioFila = UUID.randomUUID();
        when(destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario())).thenReturn(destinatarioFila);

        // Act
        var resultado = useCase.ejecutar(destinatario);

        // Assert
        assertThat(resultado).isEqualTo(destinatarioFila);
        verify(destinatarioOutputPort, never()).registrar(any());
    }

    @Test
    void debeRegistrarElCandidato_cuandoNoExisteFilaPrevia() {
        // Arrange
        var destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        when(destinatarioDeUsuarioFinder.obtener(destinatario.getUsuario()))
                .thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        var resultado = useCase.ejecutar(destinatario);

        // Assert
        assertThat(resultado).isEqualTo(destinatario.getId());
        ArgumentCaptor<DestinatarioEntity> captor = ArgumentCaptor.forClass(DestinatarioEntity.class);
        verify(destinatarioOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(destinatario.getId());
        assertThat(captor.getValue().usuario()).isEqualTo(destinatario.getUsuario());
    }
}
