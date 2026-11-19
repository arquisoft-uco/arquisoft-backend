package com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumenEvaluacionAsesorFinderImplTest {

    @Mock
    private EvaluacionAsesorOutputPort outputPort;

    @InjectMocks
    private ResumenEvaluacionAsesorFinderImpl finder;

    @Test
    void debeReconstruirElResumen_cuandoLaNotaEstaCalculada() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerResumenPorEvaluacion(evaluacion)).thenReturn(Optional.of(new ResumenEvaluacionEntity(
                new BigDecimal("1.00"), new BigDecimal("2.00"), new BigDecimal("3.00"))));

        // Act
        var resumen = finder.obtener(evaluacion);

        // Assert
        assertThat(resumen.esVacio()).isFalse();
        assertThat(resumen.getNotaAcumulada()).isEqualByComparingTo("3.00");
    }

    @Test
    void debeDevolverVacio_cuandoLaNotaNoEstaCalculada() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerResumenPorEvaluacion(evaluacion))
                .thenReturn(Optional.of(new ResumenEvaluacionEntity(null, null, null)));

        // Act
        var resumen = finder.obtener(evaluacion);

        // Assert
        assertThat(resumen).isSameAs(ResumenEvaluacionDomain.VACIO);
    }
}
