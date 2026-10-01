package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.ObservacionItemDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionesIgualesEnRevisionFinderImplTest {

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @InjectMocks
    private ObservacionesIgualesEnRevisionFinderImpl finder;

    @Test
    void debeDelegarAlPuerto_cuandoSeConsulta() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var observacionItem = ObservacionItemDomain.crear(revisionItem, "Observación válida");
        var entrada = AgregacionObservacionItemDomain.crear(observacionItem, UUID.randomUUID());
        when(observacionItemOutputPort.contarPorRevisionYObservacion(revisionItem, "Observación válida"))
                .thenReturn(2L);

        // Act
        var resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).isEqualTo(2L);
        verify(observacionItemOutputPort).contarPorRevisionYObservacion(revisionItem, "Observación válida");
    }
}
