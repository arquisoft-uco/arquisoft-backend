package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionesCuantitativasAsesorDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.ItemsCuantitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.PuntajesCuantitativosAsesorExcedenValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.DisponibilidadEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaItemsCuantitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajesVsValoresMaximosAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarEvaluacionesCuantitativasAsesorValidatorImplTest {

    private final RegistrarEvaluacionesCuantitativasAsesorValidatorImpl validator =
            new RegistrarEvaluacionesCuantitativasAsesorValidatorImpl();

    private final UUID evaluacionAsesor = UtilUUID.generarNuevoUUID();
    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID item = UtilUUID.generarNuevoUUID();

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(existenciaValida(), propiedadValida(), itemsValidos(),
                puntajesValidos(), disponibilidadLibre()))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoEvaluacionAsesorNoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, propiedadValida(), itemsValidos(),
                puntajesValidos(), disponibilidadLibre()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }

    @Test
    void debeRechazar_cuandoNoPerteneceAlAsesor() {
        // Arrange
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedad, itemsValidos(),
                puntajesValidos(), disponibilidadLibre()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeRechazar_cuandoItemsNoExisten() {
        // Arrange
        var items = new ExistenciaItemsCuantitativosAsesor(Set.of(item), Map.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), items,
                puntajesValidos(), disponibilidadLibre()))
                .isInstanceOf(ItemsCuantitativosAsesorNoEncontradosException.class);
    }

    @Test
    void debeRechazar_cuandoPuntajeExcedeValorDelItem() {
        // Arrange
        var puntajes = new PuntajesVsValoresMaximosAsesor(Map.of(item, 51), Map.of(item, 50));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), itemsValidos(),
                puntajes, disponibilidadLibre()))
                .isInstanceOf(PuntajesCuantitativosAsesorExcedenValorItemException.class);
    }

    @Test
    void debeRechazar_cuandoItemsYaRegistrados() {
        // Arrange
        var disponibilidad = new DisponibilidadEvaluacionesCuantitativasAsesor(
                evaluacionAsesor, Set.of(item), Set.of(item));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), itemsValidos(),
                puntajesValidos(), disponibilidad))
                .isInstanceOf(EvaluacionesCuantitativasAsesorDuplicadasException.class);
    }

    @Test
    void debePriorizarExistenciaDeItemsSobrePuntaje_cuandoAmbasReglasFallan() {
        // Arrange
        var items = new ExistenciaItemsCuantitativosAsesor(Set.of(item), Map.of());
        var puntajes = new PuntajesVsValoresMaximosAsesor(Map.of(item, 51), Map.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), items,
                puntajes, disponibilidadLibre()))
                .isInstanceOf(ItemsCuantitativosAsesorNoEncontradosException.class);
    }

    private ExistenciaEvaluacionAsesor existenciaValida() {
        return new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesor);
    }

    private PropiedadEvaluacionAsesor propiedadValida() {
        return new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, asesor);
    }

    private ExistenciaItemsCuantitativosAsesor itemsValidos() {
        return new ExistenciaItemsCuantitativosAsesor(Set.of(item), Map.of(item, 50));
    }

    private PuntajesVsValoresMaximosAsesor puntajesValidos() {
        return new PuntajesVsValoresMaximosAsesor(Map.of(item, 10), Map.of(item, 50));
    }

    private DisponibilidadEvaluacionesCuantitativasAsesor disponibilidadLibre() {
        return new DisponibilidadEvaluacionesCuantitativasAsesor(evaluacionAsesor, Set.of(item), Set.of());
    }
}
