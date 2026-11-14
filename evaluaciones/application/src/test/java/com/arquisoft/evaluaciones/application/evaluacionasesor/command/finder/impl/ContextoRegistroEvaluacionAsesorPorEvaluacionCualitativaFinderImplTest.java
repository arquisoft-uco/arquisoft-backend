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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinderImplTest {

    @Mock
    private ContextoRegistroEvaluacionAsesorOutputPort outputPort;

    @InjectMocks
    private ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoBloqueadoPorEvaluacionCualitativa(evaluacionCualitativa)).thenReturn(
                Optional.of(new ContextoRegistroEvaluacionAsesorEntity(
                        evaluacionAsesor, evaluacion, "EN_PROGRESO", entregable, asesor)));

        // Act
        var resultado = finder.obtener(evaluacionCualitativa);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(evaluacionAsesor);
        assertThat(resultado.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(resultado.getEstado()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
        assertThat(resultado.getEntregable()).isEqualTo(entregable);
        assertThat(resultado.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContextoBloqueadoPorEvaluacionCualitativa(evaluacionCualitativa))
                .thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacionCualitativa);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
