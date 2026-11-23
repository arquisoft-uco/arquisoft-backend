package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ConsolidacionNotasEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.evaluacion.ConsolidacionNotasEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.ResumenEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.PesosFinalizacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsolidarNotasEvaluacionUseCaseImplTest {

    @Mock
    private EvaluacionOutputPort evaluacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsolidarNotasEvaluacionUseCaseImpl useCase;

    @Test
    void debeGuardarNotasPesosYCantidad_cuandoSeConsolida() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = ResumenEvaluacionDomain.reconstruir(BigDecimal.ONE, BigDecimal.ONE, new BigDecimal("4.00"));
        var jurado = ResumenEvaluacionDomain.reconstruir(BigDecimal.ONE, BigDecimal.ONE, new BigDecimal("3.00"));
        var consolidacion = ConsolidacionNotasEvaluacionDomain.calcular(
                evaluacion, asesor, List.of(jurado, jurado), new PesosFinalizacion(BigDecimal.ONE, BigDecimal.ONE));

        // Act
        useCase.ejecutar(consolidacion);

        // Assert
        var entidad = ArgumentCaptor.forClass(ConsolidacionNotasEvaluacionEntity.class);
        verify(evaluacionOutputPort).actualizarNotas(entidad.capture());
        assertThat(entidad.getValue().evaluacion()).isEqualTo(evaluacion);
        assertThat(entidad.getValue().notaAsesor()).isEqualByComparingTo("4.00");
        assertThat(entidad.getValue().notaJurados()).isEqualByComparingTo("3.00");
        assertThat(entidad.getValue().pesoAsesor()).isEqualByComparingTo("0.3333");
        assertThat(entidad.getValue().pesoJurados()).isEqualByComparingTo("0.6667");
        assertThat(entidad.getValue().cantidadJurados()).isEqualTo(2);
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacion), eq(consolidacion.getNotaAsesor()),
                eq(consolidacion.getNotaJurados()), eq(2));
    }
}
