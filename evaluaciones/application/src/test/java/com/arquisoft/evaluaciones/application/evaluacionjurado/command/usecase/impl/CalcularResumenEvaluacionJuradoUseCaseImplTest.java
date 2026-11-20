package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ItemsCualitativosCalificadosJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ItemsCuantitativosCalificadosJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCualitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ItemCuantitativoCalificado;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.CompletitudEvaluacionJuradoDomain;
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
class CalcularResumenEvaluacionJuradoUseCaseImplTest {

    @Mock
    private ItemsCualitativosCalificadosJuradoFinder cualitativosFinder;

    @Mock
    private ItemsCuantitativosCalificadosJuradoFinder cuantitativosFinder;

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CalcularResumenEvaluacionJuradoUseCaseImpl useCase;

    @Test
    void debeCalcularYGuardarElResumen_cuandoLaEvaluacionEstaCompleta() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        when(cualitativosFinder.obtener(evaluacionJurado))
                .thenReturn(List.of(new ItemCualitativoCalificado(1, "Cumple"), new ItemCualitativoCalificado(1, "No cumple")));
        when(cuantitativosFinder.obtener(evaluacionJurado)).thenReturn(List.of(new ItemCuantitativoCalificado(5, 10)));

        // Act
        useCase.ejecutar(CompletitudEvaluacionJuradoDomain.crear(evaluacionJurado, true));

        // Assert
        verify(outputPort).actualizarResumen(evaluacionJurado, new ResumenEvaluacionEntity(
                new BigDecimal("2.50"), new BigDecimal("2.50"), new BigDecimal("2.50")));
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionJurado), eq(new BigDecimal("2.50")));
    }

    @Test
    void debeGuardarResumenSinCalcularYNoConsultarItems_cuandoLaEvaluacionNoEstaCompleta() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();

        // Act
        useCase.ejecutar(CompletitudEvaluacionJuradoDomain.crear(evaluacionJurado, false));

        // Assert
        verify(outputPort).actualizarResumen(evaluacionJurado, new ResumenEvaluacionEntity(null, null, null));
        verifyNoInteractions(cualitativosFinder, cuantitativosFinder);
    }
}
