package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.validator.ModificarPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.ModificacionPlanRealDomain;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarPlanRealUseCaseImplTest {

    @Mock
    private PlanRealOutputPort planRealOutputPort;

    @Mock
    private PlanRealExisteFinder planRealExisteFinder;

    @Mock
    private ModificarPlanRealValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarPlanRealUseCaseImpl useCase;

    @Test
    void debeConsultarValidarYActualizarEnOrden_cuandoValidacionPasa() {
        // Arrange
        var modificacion = ModificacionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), 6, "Revisar");
        var plan = modificacion.getPlanReal();
        when(planRealExisteFinder.obtener(plan)).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(planRealExisteFinder, validator, planRealOutputPort);
        orden.verify(planRealExisteFinder).obtener(plan);
        orden.verify(validator).validar(modificacion, true);
        orden.verify(planRealOutputPort).actualizarPorId(plan, 6, "Revisar");
        verify(planRealExisteFinder, times(1)).obtener(any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(plan), eq(6));
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(plan));
    }

    @Test
    void debeNoActualizarNiLogearCierre_cuandoValidatorRechaza() {
        // Arrange
        var modificacion = ModificacionPlanRealDomain.crear(UtilUUID.generarNuevoUUID(), 6, "Revisar");
        var plan = modificacion.getPlanReal();
        when(planRealExisteFinder.obtener(plan)).thenReturn(false);
        doThrow(new PlanRealNoEncontradoException(plan)).when(validator).validar(modificacion, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(PlanRealNoEncontradoException.class);
        verify(planRealOutputPort, never()).actualizarPorId(any(), anyInt(), anyString());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(plan));
    }
}
