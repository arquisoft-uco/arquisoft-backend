package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.ContextoRegistroEvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.ContextoRegistroEvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoRegistroEvaluacionAsesorFinderImplTest {

    @Mock
    private ContextoRegistroEvaluacionAsesorOutputPort outputPort;

    @InjectMocks
    private ContextoRegistroEvaluacionAsesorFinderImpl finder;

    @Test
    void debeRetornarDominio_cuandoElContextoExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entity = new ContextoRegistroEvaluacionAsesorEntity(
                evaluacionAsesor, evaluacion, "PENDIENTE", entregable, asesor);
        when(outputPort.obtenerContextoBloqueado(evaluacionAsesor)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(evaluacionAsesor);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(evaluacionAsesor);
        assertThat(resultado.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(resultado.getEstado()).isEqualTo(EstadoEvaluacion.PENDIENTE);
        assertThat(resultado.getEntregable()).isEqualTo(entregable);
        assertThat(resultado.getAsesor()).isEqualTo(asesor);
        verify(outputPort).obtenerContextoBloqueado(evaluacionAsesor);
    }

    @Test
    void debeRetornarVacio_cuandoElContextoNoExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoBloqueado(evaluacionAsesor)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacionAsesor);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
