package com.arquisoft.mapas_ruta.application.planreal.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoDeTareaFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.finder.PlanRealDeTareaExisteFinder;
import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.PlanRealOutputPort;
import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planreal.GeneracionPlanRealDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarPlanRealUseCaseImplTest {

    private static final Instant CREADO_EN = Instant.parse("2026-11-01T10:00:00Z");

    @Mock
    private PlanRealOutputPort planRealOutputPort;

    @Mock
    private PlanEstimadoDeTareaFinder planEstimadoDeTareaFinder;

    @Mock
    private PlanRealDeTareaExisteFinder planRealDeTareaExisteFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private GenerarPlanRealUseCaseImpl useCase;

    @Test
    void debeRegistrar_cuandoNoExistePlanReal() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionPlanRealDomain.crear(tarea, CREADO_EN.plusSeconds(25 * 3600));
        var estimado = PlanEstimadoDomain.reconstruir(UtilUUID.generarNuevoUUID(), tarea, 3, "Revisar", CREADO_EN);
        when(planEstimadoDeTareaFinder.obtener(tarea)).thenReturn(estimado);
        when(planRealDeTareaExisteFinder.obtener(tarea)).thenReturn(false);
        var captor = ArgumentCaptor.forClass(PlanRealEntity.class);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(planEstimadoDeTareaFinder, planRealDeTareaExisteFinder, logger, planRealOutputPort);
        orden.verify(planEstimadoDeTareaFinder).obtener(tarea);
        orden.verify(planRealDeTareaExisteFinder).obtener(tarea);
        orden.verify(logger).debug(any(ClaveMensaje.class), eq(true), eq(false));
        orden.verify(planRealOutputPort).registrar(captor.capture());
        verify(planRealOutputPort, never()).actualizarPorTarea(any(), anyInt(), anyString());
        verify(planEstimadoDeTareaFinder, times(1)).obtener(any());
        verify(planRealDeTareaExisteFinder, times(1)).obtener(any());
        assertThat(captor.getValue().tarea()).isEqualTo(tarea);
        assertThat(captor.getValue().tiempoRealDias()).isEqualTo(2);
        assertThat(captor.getValue().descripcionReal()).isEqualTo("Revisar");
        assertThat(resultado).isEqualTo(new GeneracionPlanRealResult.Generada(tarea, 2));
    }

    @Test
    void debeActualizar_cuandoYaExistePlanReal() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionPlanRealDomain.crear(tarea, CREADO_EN.plusSeconds(5 * 3600));
        var estimado = PlanEstimadoDomain.reconstruir(UtilUUID.generarNuevoUUID(), tarea, 3, "Revisar", CREADO_EN);
        when(planEstimadoDeTareaFinder.obtener(tarea)).thenReturn(estimado);
        when(planRealDeTareaExisteFinder.obtener(tarea)).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        verify(planRealOutputPort).actualizarPorTarea(tarea, 1, "Revisar");
        verify(planRealOutputPort, never()).registrar(any());
        assertThat(resultado).isEqualTo(new GeneracionPlanRealResult.Recalculada(tarea, 1));
    }

    @Test
    void debeDescartar_cuandoSinPlanEstimado() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var entrada = GeneracionPlanRealDomain.crear(tarea, CREADO_EN);
        when(planEstimadoDeTareaFinder.obtener(tarea)).thenReturn(PlanEstimadoDomain.VACIO);
        when(planRealDeTareaExisteFinder.obtener(tarea)).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        verify(logger).debug(any(ClaveMensaje.class), eq(false), eq(false));
        verify(planRealOutputPort, never()).registrar(any());
        verify(planRealOutputPort, never()).actualizarPorTarea(any(), anyInt(), anyString());
        assertThat(resultado).isEqualTo(new GeneracionPlanRealResult.Descartada(tarea));
    }
}
