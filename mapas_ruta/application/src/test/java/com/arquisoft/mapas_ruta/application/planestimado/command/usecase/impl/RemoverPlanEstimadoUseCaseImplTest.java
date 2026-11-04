package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoExisteFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.RemoverPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.RemocionPlanEstimadoDomain;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverPlanEstimadoUseCaseImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @Mock
    private PlanEstimadoExisteFinder planEstimadoExisteFinder;

    @Mock
    private RemoverPlanEstimadoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverPlanEstimadoUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoValidacionPasa() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());
        when(planEstimadoExisteFinder.obtener(remocion.getPlanEstimado())).thenReturn(true);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(planEstimadoExisteFinder, validator, planEstimadoOutputPort);
        orden.verify(planEstimadoExisteFinder).obtener(remocion.getPlanEstimado());
        orden.verify(validator).validar(remocion, true);
        orden.verify(planEstimadoOutputPort).eliminar(remocion.getPlanEstimado());
        verify(planEstimadoExisteFinder, times(1)).obtener(any());
        verifyNoMoreInteractions(planEstimadoExisteFinder, validator, planEstimadoOutputPort);
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(remocion.getPlanEstimado()));
    }

    @Test
    void debeNoEliminar_cuandoValidatorRechaza() {
        // Arrange
        var remocion = RemocionPlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID());
        when(planEstimadoExisteFinder.obtener(remocion.getPlanEstimado())).thenReturn(false);
        doThrow(new PlanEstimadoNoEncontradoException(remocion.getPlanEstimado()))
                .when(validator).validar(remocion, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
        verify(planEstimadoOutputPort, never()).eliminar(any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(remocion.getPlanEstimado()));
    }
}
