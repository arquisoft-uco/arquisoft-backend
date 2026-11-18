package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ResumenObservacionesRevisionEntity;
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
class ObservacionesDeRevisionFinderImplTest {

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @InjectMocks
    private ObservacionesDeRevisionFinderImpl finder;

    @Test
    void debeArmarElResumenYPasarPendienteAlPuerto_cuandoConsultaLaRevision() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        when(observacionItemOutputPort.resumirPorRevision(revisionItem, EstadoObservacionRevision.PENDIENTE.getId()))
                .thenReturn(new ResumenObservacionesRevisionEntity(5, 2));

        // Act
        var resultado = finder.obtener(revisionItem);

        // Assert
        assertThat(resultado.revisionItem()).isEqualTo(revisionItem);
        assertThat(resultado.total()).isEqualTo(5);
        assertThat(resultado.pendientes()).isEqualTo(2);
        verify(observacionItemOutputPort, times(1))
                .resumirPorRevision(revisionItem, EstadoObservacionRevision.PENDIENTE.getId());
    }
}
