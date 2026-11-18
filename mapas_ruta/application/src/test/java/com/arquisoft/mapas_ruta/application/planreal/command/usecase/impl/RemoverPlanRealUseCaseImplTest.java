package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.validator.RemoverPlanRealValidator;
import com.arquisoft.mapas_ruta.domain.planreal.RemocionPlanRealDomain;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverPlanRealUseCaseImplTest {

    @Mock
    private PlanRealOutputPort planRealOutputPort;

    @Mock
    private PlanRealExisteFinder planRealExisteFinder;

    @Mock
    private RemoverPlanRealValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverPlanRealUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoValidacionPasa() {
        // Arrange
        var remocion = RemocionPlanRealDomain.crear(UtilUUID.generarNuevoUUID());
        when(planRealExisteFinder.obtener(remocion.getPlanReal())).thenReturn(true);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(planRealExisteFinder, validator, planRealOutputPort);
        orden.verify(planRealExisteFinder).obtener(remocion.getPlanReal());
        orden.verify(validator).validar(remocion, true);
        orden.verify(planRealOutputPort).eliminarPorId(remocion.getPlanReal());
        verify(planRealExisteFinder, times(1)).obtener(any());
        verifyNoMoreInteractions(planRealExisteFinder, validator, planRealOutputPort);
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(remocion.getPlanReal()));
    }

    @Test
    void debeNoEliminar_cuandoValidatorRechaza() {
        // Arrange
        var remocion = RemocionPlanRealDomain.crear(UtilUUID.generarNuevoUUID());
        when(planRealExisteFinder.obtener(remocion.getPlanReal())).thenReturn(false);
        doThrow(new PlanRealNoEncontradoException(remocion.getPlanReal()))
                .when(validator).validar(remocion, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(PlanRealNoEncontradoException.class);
        verify(planRealOutputPort, never()).eliminarPorId(any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(remocion.getPlanReal()));
    }
}
