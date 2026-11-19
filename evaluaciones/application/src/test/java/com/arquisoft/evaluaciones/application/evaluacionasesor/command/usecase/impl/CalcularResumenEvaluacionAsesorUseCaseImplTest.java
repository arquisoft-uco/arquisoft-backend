package com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ItemsCualitativosCalificadosAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ItemsCuantitativosCalificadosAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.CompletitudEvaluacionAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularResumenEvaluacionAsesorUseCaseImplTest {

    @Mock
    private ItemsCualitativosCalificadosAsesorFinder cualitativosFinder;

    @Mock
    private ItemsCuantitativosCalificadosAsesorFinder cuantitativosFinder;

    @Mock
    private EvaluacionAsesorOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CalcularResumenEvaluacionAsesorUseCaseImpl useCase;

    @Test
    void debeCalcularYGuardarElResumen_cuandoLaEvaluacionEstaCompleta() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(cualitativosFinder.obtener(evaluacionAsesor))
                .thenReturn(List.of(new ItemCualitativoCalificado(1, "Cumple"), new ItemCualitativoCalificado(1, "No cumple")));
        when(cuantitativosFinder.obtener(evaluacionAsesor)).thenReturn(List.of(new ItemCuantitativoCalificado(5, 10)));

        // Act
        useCase.ejecutar(CompletitudEvaluacionAsesorDomain.crear(evaluacionAsesor, true));

        // Assert
        verify(outputPort).actualizarResumen(evaluacionAsesor, new ResumenEvaluacionEntity(
                new BigDecimal("2.50"), new BigDecimal("2.50"), new BigDecimal("2.50")));
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(new BigDecimal("2.50")));
    }

    @Test
    void debeGuardarResumenSinCalcularYNoConsultarItems_cuandoLaEvaluacionNoEstaCompleta() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();

        // Act
        useCase.ejecutar(CompletitudEvaluacionAsesorDomain.crear(evaluacionAsesor, false));

        // Assert
        verify(outputPort).actualizarResumen(evaluacionAsesor, new ResumenEvaluacionEntity(null, null, null));
        verifyNoInteractions(cualitativosFinder, cuantitativosFinder);
    }
}
