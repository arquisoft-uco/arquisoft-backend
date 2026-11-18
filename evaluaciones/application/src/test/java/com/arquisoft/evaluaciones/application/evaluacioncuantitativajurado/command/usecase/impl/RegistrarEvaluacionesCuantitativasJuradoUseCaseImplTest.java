package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.ItemsEvaluacionCuantitativaJuradoRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.model.ItemsPorEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.RegistrarEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoCompletaFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.MarcarEvaluacionJuradoEvaluadaUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ValoresMaximosItemsCuantitativosJuradoFinder;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.RegistroEvaluacionesCuantitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.ItemsCuantitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.CompletitudEvaluacionJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.ContextoRegistroEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarEvaluacionesCuantitativasJuradoUseCaseImplTest {

    @Mock
    private ContextoRegistroEvaluacionJuradoFinder contextoFinder;

    @Mock
    private ValoresMaximosItemsCuantitativosJuradoFinder valoresMaximosFinder;

    @Mock
    private ItemsEvaluacionCuantitativaJuradoRegistradosFinder itemsRegistradosFinder;

    @Mock
    private RegistrarEvaluacionesCuantitativasJuradoValidator validator;

    @Mock
    private EvaluacionCuantitativaJuradoOutputPort outputPort;

    @Mock
    private IniciarEvaluacionUseCase iniciarEvaluacionUseCase;

    @Mock
    private EvaluacionJuradoCompletaFinder completaFinder;

    @Mock
    private MarcarEvaluacionJuradoEvaluadaUseCase marcarEvaluadaUseCase;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarEvaluacionesCuantitativasJuradoUseCaseImpl useCase;

    @Test
    void debeRegistrarYMarcarEvaluada_cuandoDatosValidosYLaEvaluacionQuedaCompleta() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var item3 = UtilUUID.generarNuevoUUID();
        var registro = registroValido(evaluacionJurado, jurado, Map.of(item1, 100, item2, 200, item3, 300));
        var itemsSolicitados = Set.of(item1, item2, item3);
        var valoresMaximos = Map.of(item1, 500, item2, 500, item3, 500);
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, evaluacion, EstadoEvaluacion.PENDIENTE, entregable, jurado);

        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(valoresMaximosFinder.obtener(itemsSolicitados)).thenReturn(valoresMaximos);
        when(itemsRegistradosFinder.obtener(
                new ItemsPorEvaluacionCuantitativaJurado(evaluacionJurado, itemsSolicitados))).thenReturn(Set.of());
        when(completaFinder.obtener(evaluacionJurado)).thenReturn(true);

        // Act
        useCase.ejecutar(registro);

        // Assert
        var orden = inOrder(contextoFinder, valoresMaximosFinder, itemsRegistradosFinder, validator,
                iniciarEvaluacionUseCase, outputPort, completaFinder, marcarEvaluadaUseCase);
        orden.verify(contextoFinder, times(1)).obtener(evaluacionJurado);
        orden.verify(valoresMaximosFinder, times(1)).obtener(itemsSolicitados);
        orden.verify(itemsRegistradosFinder, times(1)).obtener(any(ItemsPorEvaluacionCuantitativaJurado.class));
        orden.verify(validator).validar(
                eq(new ExistenciaEvaluacionJurado(evaluacionJurado, true)),
                eq(new PropiedadEvaluacionJurado(evaluacionJurado, jurado, jurado)),
                eq(new ExistenciaItemsCuantitativosJurado(itemsSolicitados, valoresMaximos)),
                eq(new PuntajesVsValoresMaximosJurado(Map.of(item1, 100, item2, 200, item3, 300), valoresMaximos)),
                eq(new DisponibilidadEvaluacionesCuantitativasJurado(evaluacionJurado, itemsSolicitados, Set.of())));
        orden.verify(iniciarEvaluacionUseCase).ejecutar(any(InicioEvaluacionDomain.class));
        orden.verify(outputPort, times(1)).registrarTodas(any());
        orden.verify(completaFinder).obtener(evaluacionJurado);
        orden.verify(marcarEvaluadaUseCase).ejecutar(any(CompletitudEvaluacionJuradoDomain.class));

        verificarInicioEvaluacion(evaluacion);
        verificarEntidadesRegistradas(item1, item2, item3);

        var completitudCaptor = ArgumentCaptor.forClass(CompletitudEvaluacionJuradoDomain.class);
        verify(marcarEvaluadaUseCase).ejecutar(completitudCaptor.capture());
        assertThat(completitudCaptor.getValue().getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(completitudCaptor.getValue().isCompleta()).isTrue();

        verify(logger).info(EvaluacionCuantitativaJuradoKey.LOG_REGISTRANDO_LOTE, evaluacionJurado, 3);
        verify(logger).debug(any(ClaveMensaje.class), eq(evaluacionJurado), eq(true), eq(3), eq(0));
        verify(logger).info(EvaluacionCuantitativaJuradoKey.LOG_LOTE_REGISTRADO, evaluacionJurado, 3);
    }

    @Test
    void noDebePersistir_cuandoElValidatorRechaza() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var registro = registroValido(evaluacionJurado, jurado, Map.of(item, 100));
        var contexto = ContextoRegistroEvaluacionJuradoDomain.reconstruir(
                evaluacionJurado, UtilUUID.generarNuevoUUID(), EstadoEvaluacion.PENDIENTE,
                UtilUUID.generarNuevoUUID(), jurado);
        when(contextoFinder.obtener(evaluacionJurado)).thenReturn(contexto);
        when(valoresMaximosFinder.obtener(Set.of(item))).thenReturn(Map.of());
        when(itemsRegistradosFinder.obtener(any(ItemsPorEvaluacionCuantitativaJurado.class))).thenReturn(Set.of());
        doThrow(new ItemsCuantitativosJuradoNoEncontradosException(Set.of(item)))
                .when(validator).validar(any(), any(), any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(ItemsCuantitativosJuradoNoEncontradosException.class);
        verify(iniciarEvaluacionUseCase, never()).ejecutar(any());
        verify(outputPort, never()).registrarTodas(any());
        verify(completaFinder, never()).obtener(any());
        verify(marcarEvaluadaUseCase, never()).ejecutar(any());
        verify(logger, never()).info(EvaluacionCuantitativaJuradoKey.LOG_LOTE_REGISTRADO, evaluacionJurado, 1);
    }

    private void verificarInicioEvaluacion(UUID evaluacion) {
        var inicioCaptor = ArgumentCaptor.forClass(InicioEvaluacionDomain.class);
        verify(iniciarEvaluacionUseCase).ejecutar(inicioCaptor.capture());
        assertThat(inicioCaptor.getValue().getEvaluacion()).isEqualTo(evaluacion);
        assertThat(inicioCaptor.getValue().getEstadoActual()).isEqualTo(EstadoEvaluacion.PENDIENTE);
    }

    private void verificarEntidadesRegistradas(UUID item1, UUID item2, UUID item3) {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<EvaluacionCuantitativaJuradoEntity>> entidadesCaptor = ArgumentCaptor.forClass(List.class);
        verify(outputPort).registrarTodas(entidadesCaptor.capture());
        assertThat(entidadesCaptor.getValue())
                .extracting(EvaluacionCuantitativaJuradoEntity::item, EvaluacionCuantitativaJuradoEntity::puntaje)
                .containsExactlyInAnyOrder(tuple(item1, 100), tuple(item2, 200), tuple(item3, 300));
    }

    private static RegistroEvaluacionesCuantitativasJuradoDomain registroValido(
            UUID evaluacionJurado, UUID jurado, Map<UUID, Integer> puntajesPorItem) {
        var evaluaciones = puntajesPorItem.entrySet().stream()
                .map(par -> EvaluacionCuantitativaJuradoDomain.crear(evaluacionJurado, par.getKey(), par.getValue()))
                .toList();
        return RegistroEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, jurado, evaluaciones);
    }
}
