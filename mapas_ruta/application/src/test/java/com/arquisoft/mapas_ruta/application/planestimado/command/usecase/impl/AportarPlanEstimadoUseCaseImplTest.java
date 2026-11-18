package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
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
    private PlanEstimadoPorIdFinder planEstimadoPorIdFinder;

    @Mock
    private TareaPorIdFinder tareaPorIdFinder;

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
        var planPersistido = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 3, null);
        var tarea = TareaDomain.reconstruir(planPersistido.getTarea(), UtilUUID.generarNuevoUUID(), "Tarea",
                UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
        when(planEstimadoPorIdFinder.obtener(plan)).thenReturn(planPersistido);
        when(tareaPorIdFinder.obtener(planPersistido.getTarea())).thenReturn(tarea);

        // Act
        useCase.ejecutar(aporte);

        // Assert
        var orden = inOrder(planEstimadoPorIdFinder, tareaPorIdFinder, validator, planEstimadoOutputPort);
        orden.verify(planEstimadoPorIdFinder).obtener(plan);
        orden.verify(validator).validar(aporte, true, tarea);
        orden.verify(planEstimadoOutputPort).actualizarDescripcion(plan, "Revisar");
        verify(planEstimadoPorIdFinder, times(1)).obtener(any());
        verify(tareaPorIdFinder, times(1)).obtener(any());
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(plan));
    }

    @Test
    void debeNoActualizarNiLogearCierre_cuandoValidatorRechaza() {
        // Arrange
        var aporte = AportePlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), "Revisar");
        var plan = aporte.getPlanEstimado();
        when(planEstimadoPorIdFinder.obtener(plan)).thenReturn(PlanEstimadoDomain.VACIO);
        when(tareaPorIdFinder.obtener(PlanEstimadoDomain.VACIO.getTarea())).thenReturn(TareaDomain.VACIO);
        doThrow(new PlanEstimadoNoEncontradoException(plan)).when(validator).validar(aporte, false, TareaDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(aporte))
                .isInstanceOf(PlanEstimadoNoEncontradoException.class);
        verify(planEstimadoOutputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(plan));
    }
}
