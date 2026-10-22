package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CriteriosCualitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionesCualitativasAsesorDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.ItemsCualitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.DisponibilidadEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaItemsCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarEvaluacionesCualitativasAsesorValidatorImplTest {

    private final RegistrarEvaluacionesCualitativasAsesorValidatorImpl validator =
            new RegistrarEvaluacionesCualitativasAsesorValidatorImpl();

    private final UUID evaluacionAsesor = UtilUUID.generarNuevoUUID();
    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final UUID item = UtilUUID.generarNuevoUUID();
    private final UUID criterio = UtilUUID.generarNuevoUUID();

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesor);
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, asesor);
        var items = new ExistenciaItemsCualitativosAsesor(Set.of(item), Set.of(item));
        var criterios = new ExistenciaCriteriosCualitativosAsesor(Set.of(criterio), Set.of(criterio));
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasAsesor(evaluacionAsesor, Set.of(item), Set.of());

        // Act & Assert
        assertThatCode(() -> validator.validar(existencia, propiedad, items, criterios, disponibilidad))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoEvaluacionAsesorNoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto());
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto(), asesor);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, propiedad, itemsValidos(), criteriosValidos(),
                disponibilidadLibre()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }

    @Test
    void debeRechazar_cuandoNoPerteneceAlAsesor() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesor);
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, propiedad, itemsValidos(), criteriosValidos(),
                disponibilidadLibre()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeRechazar_cuandoItemsNoExisten() {
        // Arrange
        var items = new ExistenciaItemsCualitativosAsesor(Set.of(item), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), items, criteriosValidos(),
                disponibilidadLibre()))
                .isInstanceOf(ItemsCualitativosAsesorNoEncontradosException.class);
    }

    @Test
    void debeRechazar_cuandoCriteriosNoExisten() {
        // Arrange
        var criterios = new ExistenciaCriteriosCualitativosAsesor(Set.of(criterio), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), itemsValidos(), criterios,
                disponibilidadLibre()))
                .isInstanceOf(CriteriosCualitativosAsesorNoEncontradosException.class);
    }

    @Test
    void debeRechazar_cuandoItemsYaRegistrados() {
        // Arrange
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasAsesor(
                evaluacionAsesor, Set.of(item), Set.of(item));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existenciaValida(), propiedadValida(), itemsValidos(),
                criteriosValidos(), disponibilidad))
                .isInstanceOf(EvaluacionesCualitativasAsesorDuplicadasException.class);
    }

    @Test
    void debePriorizarExistenciaSobrePropiedadYContenido_cuandoVariasReglasFallan() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto());
        var propiedad = new PropiedadEvaluacionAsesor(evaluacionAsesor, UtilUUID.obtenerUUIDPorDefecto(), asesor);
        var items = new ExistenciaItemsCualitativosAsesor(Set.of(item), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia, propiedad, items, criteriosValidos(),
                disponibilidadLibre()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }

    private ExistenciaEvaluacionAsesor existenciaValida() {
        return new ExistenciaEvaluacionAsesor(evaluacionAsesor, asesor);
    }

    private PropiedadEvaluacionAsesor propiedadValida() {
        return new PropiedadEvaluacionAsesor(evaluacionAsesor, asesor, asesor);
    }

    private ExistenciaItemsCualitativosAsesor itemsValidos() {
        return new ExistenciaItemsCualitativosAsesor(Set.of(item), Set.of(item));
    }

    private ExistenciaCriteriosCualitativosAsesor criteriosValidos() {
        return new ExistenciaCriteriosCualitativosAsesor(Set.of(criterio), Set.of(criterio));
    }

    private DisponibilidadEvaluacionesCualitativasAsesor disponibilidadLibre() {
        return new DisponibilidadEvaluacionesCualitativasAsesor(evaluacionAsesor, Set.of(item), Set.of());
    }
}
