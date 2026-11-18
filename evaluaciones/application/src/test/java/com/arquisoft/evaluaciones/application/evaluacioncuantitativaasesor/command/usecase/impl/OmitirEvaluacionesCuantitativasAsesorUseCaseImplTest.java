package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemsEvaluacionCuantitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.OmitirEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.ContextoRegistroEvaluacionAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.OmisionEvaluacionesCuantitativasAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.OmisionEvaluacionAsesorFinalizadaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

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
class OmitirEvaluacionesCuantitativasAsesorUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionAsesorFinder contextoFinder;

    @Mock
    private ItemsEvaluacionCuantitativaAsesorRegistradosFinder itemsRegistradosFinder;

    @Mock
    private OmitirEvaluacionesCuantitativasAsesorValidator validator;

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private OmitirEvaluacionesCuantitativasAsesorUseCaseImpl useCase;

    @Test
    void debeOmitir_cuandoValidacionPasa() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var item3 = UtilUUID.generarNuevoUUID();
        var items = Set.of(item1, item2, item3);
        var omision = OmisionEvaluacionesCuantitativasAsesorDomain.crear(
                evaluacionAsesor, asesor, List.of(item1, item2, item3));
        var contexto = ContextoRegistroEvaluacionAsesorDomain.reconstruir(
                evaluacionAsesor, UtilUUID.generarNuevoUUID(), EstadoEvaluacion.EN_PROGRESO,
                UtilUUID.generarNuevoUUID(), asesor);
        when(contextoFinder.obtener(evaluacionAsesor)).thenReturn(contexto);
        when(itemsRegistradosFinder.obtener(new ItemsPorEvaluacionCuantitativaAsesor(evaluacionAsesor, items)))
                .thenReturn(items);

        // Act
        useCase.ejecutar(omision);

        // Assert
        var orden = inOrder(contextoFinder, itemsRegistradosFinder, validator, outputPort);
        orden.verify(contextoFinder).obtener(evaluacionAsesor);
        orden.verify(itemsRegistradosFinder).obtener(any(ItemsPorEvaluacionCuantitativaAsesor.class));
        orden.verify(validator).validar(any(), any(), any(), any());
        orden.verify(outputPort).eliminarPorItems(evaluacionAsesor, items);
        verify(contextoFinder, times(1)).obtener(any());
        verify(itemsRegistradosFinder, times(1)).obtener(any());
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(true),
                eq(EstadoEvaluacion.EN_PROGRESO.getId()), eq(3));
        verify(logger, times(2)).info(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(3));
    }

    @Test
    void noDebeEliminar_cuandoValidatorRechaza() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var omision = OmisionEvaluacionesCuantitativasAsesorDomain.crear(
                evaluacionAsesor, UtilUUID.generarNuevoUUID(), List.of(item));
        when(contextoFinder.obtener(evaluacionAsesor)).thenReturn(ContextoRegistroEvaluacionAsesorDomain.VACIO);
        when(itemsRegistradosFinder.obtener(any(ItemsPorEvaluacionCuantitativaAsesor.class))).thenReturn(Set.of());
        doThrow(new OmisionEvaluacionAsesorFinalizadaException(evaluacionAsesor))
                .when(validator).validar(any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(omision))
                .isInstanceOf(OmisionEvaluacionAsesorFinalizadaException.class);
        verify(outputPort, never()).eliminarPorItems(any(), any());
        verify(logger, times(1)).info(any(ClaveMensaje.class), eq(evaluacionAsesor), eq(1));
    }
}
