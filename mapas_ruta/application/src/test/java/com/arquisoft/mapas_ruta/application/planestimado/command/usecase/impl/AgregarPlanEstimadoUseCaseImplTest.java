package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaDeTareaFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.finder.DiasEstimadosAcumuladosDeMapaRutaFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoDeTareaExisteFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity.PlanEstimadoEntity;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AgregarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarPlanEstimadoUseCaseImplTest {

    @Mock
    private PlanEstimadoOutputPort planEstimadoOutputPort;

    @Mock
    private TareaPorIdFinder tareaPorIdFinder;

    @Mock
    private MapaRutaDeTareaFinder mapaRutaDeTareaFinder;

    @Mock
    private PlanEstimadoDeTareaExisteFinder planEstimadoDeTareaExisteFinder;

    @Mock
    private DiasEstimadosAcumuladosDeMapaRutaFinder diasEstimadosAcumuladosDeMapaRutaFinder;

    @Mock
    private AgregarPlanEstimadoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarPlanEstimadoUseCaseImpl useCase;

    @Test
    void debeConsultarValidarYRegistrarEnOrden_cuandoValidacionPasa() {
        // Arrange
        var mapa = MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        var tarea = TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), mapa.getId(), "Redactar",
                UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
        var plan = PlanEstimadoDomain.crear(tarea.getId(), 5, "Revisar");
        when(tareaPorIdFinder.obtener(tarea.getId())).thenReturn(tarea);
        when(mapaRutaDeTareaFinder.obtener(tarea.getId())).thenReturn(mapa);
        when(planEstimadoDeTareaExisteFinder.obtener(tarea.getId())).thenReturn(false);
        when(diasEstimadosAcumuladosDeMapaRutaFinder.obtener(tarea.getId())).thenReturn(7L);

        // Act
        var id = useCase.ejecutar(plan);

        // Assert
        assertThat(id).isEqualTo(plan.getId());
        var orden = inOrder(tareaPorIdFinder, mapaRutaDeTareaFinder, planEstimadoDeTareaExisteFinder,
                diasEstimadosAcumuladosDeMapaRutaFinder, validator, planEstimadoOutputPort);
        orden.verify(tareaPorIdFinder).obtener(tarea.getId());
        orden.verify(mapaRutaDeTareaFinder).obtener(tarea.getId());
        orden.verify(planEstimadoDeTareaExisteFinder).obtener(tarea.getId());
        orden.verify(diasEstimadosAcumuladosDeMapaRutaFinder).obtener(tarea.getId());
        orden.verify(validator).validar(plan, tarea, mapa, false, 7L);
        orden.verify(planEstimadoOutputPort).registrar(
                new PlanEstimadoEntity(plan.getId(), tarea.getId(), 5, "Revisar"));
        verify(tareaPorIdFinder, times(1)).obtener(any());
        verify(mapaRutaDeTareaFinder, times(1)).obtener(any());
        verify(planEstimadoDeTareaExisteFinder, times(1)).obtener(any());
        verify(diasEstimadosAcumuladosDeMapaRutaFinder, times(1)).obtener(any());
        verify(logger).info(any(ClaveMensaje.class), eq(plan.getId()), eq(tarea.getId()));
    }

    @Test
    void debeNoRegistrarNiLogearCierre_cuandoValidatorRechaza() {
        // Arrange
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 5, null);
        when(tareaPorIdFinder.obtener(plan.getTarea())).thenReturn(TareaDomain.VACIO);
        when(mapaRutaDeTareaFinder.obtener(plan.getTarea())).thenReturn(MapaRutaDomain.VACIO);
        when(planEstimadoDeTareaExisteFinder.obtener(plan.getTarea())).thenReturn(false);
        when(diasEstimadosAcumuladosDeMapaRutaFinder.obtener(plan.getTarea())).thenReturn(0L);
        doThrow(new TareaNoEncontradaException(plan.getTarea())).when(validator)
                .validar(plan, TareaDomain.VACIO, MapaRutaDomain.VACIO, false, 0L);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(plan)).isInstanceOf(TareaNoEncontradaException.class);
        verify(planEstimadoOutputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(plan.getId()), eq(plan.getTarea()));
    }
}
