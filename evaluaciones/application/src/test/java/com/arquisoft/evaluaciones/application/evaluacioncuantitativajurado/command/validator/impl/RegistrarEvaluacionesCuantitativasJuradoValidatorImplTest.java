package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.ItemsCuantitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajesCuantitativosJuradoExcedenValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarEvaluacionesCuantitativasJuradoValidatorImplTest {

    private final RegistrarEvaluacionesCuantitativasJuradoValidatorImpl validator =
            new RegistrarEvaluacionesCuantitativasJuradoValidatorImpl();

    private final UUID evaluacionJurado = UtilUUID.generarNuevoUUID();
    private final UUID jurado = UtilUUID.generarNuevoUUID();
    private final UUID item = UtilUUID.generarNuevoUUID();

    private ExistenciaEvaluacionJurado existenciaOk() {
        return new ExistenciaEvaluacionJurado(evaluacionJurado, true);
    }

    private PropiedadEvaluacionJurado propiedadOk() {
        return new PropiedadEvaluacionJurado(evaluacionJurado, jurado, jurado);
    }

    private ExistenciaItemsCuantitativosJurado itemsOk() {
        return new ExistenciaItemsCuantitativosJurado(Set.of(item), Map.of(item, 100));
    }

    private PuntajesVsValoresMaximosJurado puntajesOk() {
        return new PuntajesVsValoresMaximosJurado(Map.of(item, 100), Map.of(item, 100));
    }

    private DisponibilidadEvaluacionesCuantitativasJurado disponibilidadOk() {
        return new DisponibilidadEvaluacionesCuantitativasJurado(evaluacionJurado, Set.of(item), Set.of());
    }

    @Test
    void noDebeLanzar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                existenciaOk(), propiedadOk(), itemsOk(), puntajesOk(), disponibilidadOk()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarPorExistencia_antesQueCualquierOtraRegla() {
        // Arrange
        var inexistente = new ExistenciaEvaluacionJurado(evaluacionJurado, false);
        var ajena = new PropiedadEvaluacionJurado(evaluacionJurado, UtilUUID.generarNuevoUUID(), jurado);
        var sinItems = new ExistenciaItemsCuantitativosJurado(Set.of(item), Map.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                inexistente, ajena, sinItems, puntajesOk(), disponibilidadOk()))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }

    @Test
    void debeRechazarPorPropiedad_antesQueItems() {
        // Arrange
        var ajena = new PropiedadEvaluacionJurado(evaluacionJurado, UtilUUID.generarNuevoUUID(), jurado);
        var sinItems = new ExistenciaItemsCuantitativosJurado(Set.of(item), Map.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                existenciaOk(), ajena, sinItems, puntajesOk(), disponibilidadOk()))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
    }

    @Test
    void debeRechazarPorItemsInexistentes_antesQuePuntajes() {
        // Arrange
        var sinItems = new ExistenciaItemsCuantitativosJurado(Set.of(item), Map.of());
        var puntajeExcedido = new PuntajesVsValoresMaximosJurado(Map.of(item, 101), Map.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                existenciaOk(), propiedadOk(), sinItems, puntajeExcedido, disponibilidadOk()))
                .isInstanceOf(ItemsCuantitativosJuradoNoEncontradosException.class);
    }

    @Test
    void debeRechazarPorPuntajeExcedido_antesQueDuplicados() {
        // Arrange
        var puntajeExcedido = new PuntajesVsValoresMaximosJurado(Map.of(item, 101), Map.of(item, 100));
        var yaRegistrado = new DisponibilidadEvaluacionesCuantitativasJurado(
                evaluacionJurado, Set.of(item), Set.of(item));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                existenciaOk(), propiedadOk(), itemsOk(), puntajeExcedido, yaRegistrado))
                .isInstanceOf(PuntajesCuantitativosJuradoExcedenValorItemException.class);
    }

    @Test
    void debeRechazarPorDuplicados_cuandoUnItemYaFueEvaluado() {
        // Arrange
        var yaRegistrado = new DisponibilidadEvaluacionesCuantitativasJurado(
                evaluacionJurado, Set.of(item), Set.of(item));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                existenciaOk(), propiedadOk(), itemsOk(), puntajesOk(), yaRegistrado))
                .isInstanceOf(EvaluacionesCuantitativasJuradoDuplicadasException.class);
    }
}
