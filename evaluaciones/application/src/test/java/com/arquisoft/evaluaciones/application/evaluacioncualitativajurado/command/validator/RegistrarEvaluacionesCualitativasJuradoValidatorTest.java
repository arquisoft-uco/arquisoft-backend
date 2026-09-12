package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.impl.RegistrarEvaluacionesCualitativasJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriteriosCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.ItemsCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarEvaluacionesCualitativasJuradoValidatorTest {

    private final RegistrarEvaluacionesCualitativasJuradoValidatorImpl validator =
            new RegistrarEvaluacionesCualitativasJuradoValidatorImpl();

    @Test
    void debePermitirAcceso_cuandoEvaluacionExisteYActorEsPropietario() {
        // Arrange
        UUID actor = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionJurado(UUID.randomUUID(), true);
        var propiedad = new PropiedadEvaluacionJurado(actor, actor);

        // Act & Assert
        assertThatCode(() -> validator.validarAcceso(existencia, propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeRechazarAccesoPorExistencia_cuandoLaEvaluacionDeJuradoNoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionJurado(UUID.randomUUID(), false);
        var propiedad = new PropiedadEvaluacionJurado(UUID.randomUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarAcceso(existencia, propiedad))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }

    @Test
    void debeRechazarAccesoPorPropiedad_cuandoLaEvaluacionExisteYElActorNoEsElPropietario() {
        // Arrange
        var existencia = new ExistenciaEvaluacionJurado(UUID.randomUUID(), true);
        var propiedad = new PropiedadEvaluacionJurado(UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validarAcceso(existencia, propiedad))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
    }

    @Test
    void debePermitirContenido_cuandoItemsYCriteriosExistenYNoHayDuplicados() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var items = new ExistenciaItemsCualitativosJurado(Set.of(item), Set.of(item));
        var criterios = new ExistenciaCriteriosCualitativosJurado(Set.of(criterio), Set.of(criterio));
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(item), Set.of());

        // Act & Assert
        assertThatCode(() -> validator.validarContenido(items, criterios, disponibilidad))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarContenidoPorItems_cuandoUnItemSolicitadoNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        var items = new ExistenciaItemsCualitativosJurado(Set.of(item), Set.of());
        var criterios = new ExistenciaCriteriosCualitativosJurado(Set.of(), Set.of());
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(item), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validarContenido(items, criterios, disponibilidad))
                .isInstanceOf(ItemsCualitativosJuradoNoEncontradosException.class);
    }

    @Test
    void debeRechazarContenidoPorCriterios_cuandoItemsExistenPeroUnCriterioNo() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var items = new ExistenciaItemsCualitativosJurado(Set.of(item), Set.of(item));
        var criterios = new ExistenciaCriteriosCualitativosJurado(Set.of(criterio), Set.of());
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(item), Set.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validarContenido(items, criterios, disponibilidad))
                .isInstanceOf(CriteriosCualitativosJuradoNoEncontradosException.class);
    }

    @Test
    void debeRechazarContenidoPorDuplicados_cuandoItemsYCriteriosExistenPeroYaFueronRegistrados() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        var items = new ExistenciaItemsCualitativosJurado(Set.of(item), Set.of(item));
        var criterios = new ExistenciaCriteriosCualitativosJurado(Set.of(criterio), Set.of(criterio));
        var disponibilidad = new DisponibilidadEvaluacionesCualitativasJurado(
                UUID.randomUUID(), Set.of(item), Set.of(item));

        // Act & Assert
        assertThatThrownBy(() -> validator.validarContenido(items, criterios, disponibilidad))
                .isInstanceOf(EvaluacionesCualitativasJuradoDuplicadasException.class);
    }
}
