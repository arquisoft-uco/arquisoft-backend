package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemDeEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.CambiarPuntajeEvaluacionCuantitativaAsesorValidator;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorPorIdFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarPuntajeEvaluacionCuantitativaAsesorUseCaseImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @Mock
    private ContextoEvaluacionCuantitativaAsesorFinder contextoFinder;

    @Mock
    private ItemDeEvaluacionCuantitativaAsesorFinder itemFinder;

    @Mock
    private ItemCuantitativoAsesorPorIdFinder itemPorIdFinder;

    @Mock
    private CambiarPuntajeEvaluacionCuantitativaAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarPuntajeEvaluacionCuantitativaAsesorUseCaseImpl useCase;

    @Test
    void debeActualizarPuntaje_cuandoValidacionPasa() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var cambio = CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(id, asesor, 250);
        var contexto = ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                id, evaluacionAsesor, asesor, EstadoEvaluacion.EN_PROGRESO);
        var itemId = UtilUUID.generarNuevoUUID();
        var item = ItemCuantitativoAsesorDomain.reconstruir(
                itemId, "Rigor", "Descripción", UtilUUID.generarNuevoUUID(), 300);
        when(contextoFinder.obtener(id)).thenReturn(contexto);
        when(itemFinder.obtener(id)).thenReturn(itemId);
        when(itemPorIdFinder.obtener(itemId)).thenReturn(item);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(logger, contextoFinder, itemFinder, itemPorIdFinder, validator, outputPort);
        orden.verify(logger).info(EvaluacionCuantitativaAsesorKey.LOG_CAMBIANDO_PUNTAJE, id, 250);
        orden.verify(contextoFinder).obtener(id);
        orden.verify(itemFinder).obtener(id);
        orden.verify(itemPorIdFinder).obtener(itemId);
        orden.verify(logger).debug(EvaluacionCuantitativaAsesorKey.LOG_VERIFICACION_CAMBIAR_PUNTAJE,
                true, EstadoEvaluacion.EN_PROGRESO.getId(), 300);
        orden.verify(validator).validar(cambio, contexto, item);
        orden.verify(outputPort).actualizarPuntaje(id, 250);
        orden.verify(logger).info(EvaluacionCuantitativaAsesorKey.LOG_PUNTAJE_CAMBIADO, id);
        verify(contextoFinder, times(1)).obtener(any());
        verify(itemFinder, times(1)).obtener(any());
        verify(itemPorIdFinder, times(1)).obtener(any());
    }
}
