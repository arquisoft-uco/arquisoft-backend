package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionesSinCerrarEnRevisionFinderImplTest {

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @InjectMocks
    private ObservacionesSinCerrarEnRevisionFinderImpl finder;

    @Test
    void debeDelegarConEstadoCerrado_cuandoCuentaLasOtrasSinCerrar() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        when(observacionItemOutputPort.contarOtrasSinCerrarEnRevision(
                observacionItem, EstadoObservacionRevision.CERRADO.getId())).thenReturn(3L);

        // Act
        var resultado = finder.obtener(observacionItem);

        // Assert
        assertThat(resultado).isEqualTo(3L);
        verify(observacionItemOutputPort, times(1)).contarOtrasSinCerrarEnRevision(
                observacionItem, EstadoObservacionRevision.CERRADO.getId());
    }
}
