package com.arquisoft.solicitudes.application.remitente.command.usecase.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.remitente.command.finder.RemitenteDeUsuarioFinder;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
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
class RegistrarRemitenteUseCaseImplTest {

    @Mock private RemitenteDeUsuarioFinder remitenteDeUsuarioFinder;
    @Mock private RemitenteOutputPort remitenteOutputPort;

    private RegistrarRemitenteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarRemitenteUseCaseImpl(remitenteDeUsuarioFinder, remitenteOutputPort);
    }

    @Test
    void debeReutilizarLaFilaExistente_cuandoElFinderDevuelveUnId() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        var remitenteFila = UUID.randomUUID();
        when(remitenteDeUsuarioFinder.obtener(remitente.getUsuario())).thenReturn(remitenteFila);

        // Act
        var resultado = useCase.ejecutar(remitente);

        // Assert
        assertThat(resultado).isEqualTo(remitenteFila);
        verify(remitenteOutputPort, never()).registrar(any());
    }

    @Test
    void debeRegistrarElCandidato_cuandoNoExisteFilaPrevia() {
        // Arrange
        var remitente = RemitenteDomain.crear(UUID.randomUUID());
        when(remitenteDeUsuarioFinder.obtener(remitente.getUsuario()))
                .thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        var resultado = useCase.ejecutar(remitente);

        // Assert
        assertThat(resultado).isEqualTo(remitente.getId());
        ArgumentCaptor<RemitenteEntity> captor = ArgumentCaptor.forClass(RemitenteEntity.class);
        verify(remitenteOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(remitente.getId());
        assertThat(captor.getValue().usuario()).isEqualTo(remitente.getUsuario());
    }
}
