package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoExisteFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AportarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.AportePlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AportarPlanEstimadoUseCaseImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @Mock
    private PlanEstimadoExisteFinder planEstimadoExisteFinder;

    @Mock
    private AportarPlanEstimadoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AportarPlanEstimadoUseCaseImpl useCase;

    @Test
    void debeConsultarValidarYActualizarEnOrden_cuandoValidacionPasa() {
        // Arrange
        var aporte = AportePlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var plan = aporte.getPlanEstimado();
        when(planEstimadoExisteFinder.obtener(plan)).thenReturn(true);

        // Act
        useCase.ejecutar(aporte);

        // Assert
        var orden = inOrder(planEstimadoExisteFinder, validator, planEstimadoOutputPort);
        orden.verify(planEstimadoExisteFinder).obtener(plan);
        orden.verify(validator).validar(aporte, true);
        orden.verify(planEstimadoOutputPort).actualizarDescripcion(plan, "Revisar");
        verify(planEstimadoExisteFinder, times(1)).obtener(any());
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(plan));
    }

    @Test
    void debeNoActualizarNiLogearCierre_cuandoValidatorRechaza() {
        // Arrange
        var aporte = AportePlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var plan = aporte.getPlanEstimado();
        when(planEstimadoExisteFinder.obtener(plan)).thenReturn(false);
        doThrow(new PlanEstimadoNoEncontradoException(plan)).when(validator).validar(aporte, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(aporte))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
        verify(planEstimadoOutputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(plan));
    }
}
