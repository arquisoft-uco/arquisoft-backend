package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.validator.AportarPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.AportePlanRealDomain;
import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
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
class AportarPlanRealUseCaseImplTest {

    @Mock
    private PlanRealOutputPort planRealOutputPort;

    @Mock
    private PlanRealExisteFinder planRealExisteFinder;

    @Mock
    private AportarPlanRealValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AportarPlanRealUseCaseImpl useCase;

    @Test
    void debeConsultarValidarYActualizarEnOrden_cuandoValidacionPasa() {
        // Arrange
        var aporte = AportePlanRealDomain.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var plan = aporte.getPlanReal();
        when(planRealExisteFinder.obtener(plan)).thenReturn(true);

        // Act
        useCase.ejecutar(aporte);

        // Assert
        var orden = inOrder(planRealExisteFinder, validator, planRealOutputPort);
        orden.verify(planRealExisteFinder).obtener(plan);
        orden.verify(validator).validar(aporte, true);
        orden.verify(planRealOutputPort).actualizarDescripcionPorId(plan, "Revisar");
        verify(planRealExisteFinder, times(1)).obtener(any());
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(plan));
    }

    @Test
    void debeNoActualizarNiLogearCierre_cuandoValidatorRechaza() {
        // Arrange
        var aporte = AportePlanRealDomain.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var plan = aporte.getPlanReal();
        when(planRealExisteFinder.obtener(plan)).thenReturn(false);
        doThrow(new PlanRealNoEncontradoException(plan)).when(validator).validar(aporte, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(aporte))
                .isInstanceOf(PlanRealNoEncontradoException.class);
        verify(planRealOutputPort, never()).actualizarDescripcionPorId(any(), anyString());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(plan));
    }
}
