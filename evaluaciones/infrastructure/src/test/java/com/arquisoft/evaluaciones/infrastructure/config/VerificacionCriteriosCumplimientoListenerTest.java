package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.repository.CriterioItemCualitativoAsesorCommandRepository;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository.CriterioItemCualitativoJuradoCommandRepository;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.EvaluacionesCriterios;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificacionCriteriosCumplimientoListenerTest {

    @Mock
    private CriterioItemCualitativoJuradoCommandRepository criterioJuradoRepository;

    @Mock
    private CriterioItemCualitativoAsesorCommandRepository criterioAsesorRepository;

    @Mock
    private AppLogger appLogger;

    @InjectMocks
    private VerificacionCriteriosCumplimientoListener listener;

    @Test
    void debeAvisarPorCadaCriterioAusente_cuandoFaltaCumpleEnUnCatalogoYNoCumpleEnOtro() {
        // Arrange
        when(criterioJuradoRepository.existePorNombreNormalizado(EvaluacionesCriterios.CUMPLE)).thenReturn(false);
        when(criterioJuradoRepository.existePorNombreNormalizado(EvaluacionesCriterios.NO_CUMPLE)).thenReturn(true);
        when(criterioAsesorRepository.existePorNombreNormalizado(EvaluacionesCriterios.CUMPLE)).thenReturn(true);
        when(criterioAsesorRepository.existePorNombreNormalizado(EvaluacionesCriterios.NO_CUMPLE)).thenReturn(false);

        // Act
        listener.verificar();

        // Assert
        verify(appLogger).warn(eq(EvaluacionKey.LOG_CRITERIO_CUMPLIMIENTO_AUSENTE),
                eq("criterio_item_cualitativo_jurado"), eq(EvaluacionesCriterios.CUMPLE));
        verify(appLogger).warn(eq(EvaluacionKey.LOG_CRITERIO_CUMPLIMIENTO_AUSENTE),
                eq("criterio_item_cualitativo_asesor"), eq(EvaluacionesCriterios.NO_CUMPLE));
    }

    @Test
    void debeNoAvisar_cuandoLosDosCriteriosExistenEnAmbosCatalogos() {
        // Arrange
        when(criterioJuradoRepository.existePorNombreNormalizado(anyString())).thenReturn(true);
        when(criterioAsesorRepository.existePorNombreNormalizado(anyString())).thenReturn(true);

        // Act
        listener.verificar();

        // Assert
        verify(appLogger, never()).warn(any(com.arquisoft.shared.message.ClaveMensaje.class), any(), any());
    }
}
