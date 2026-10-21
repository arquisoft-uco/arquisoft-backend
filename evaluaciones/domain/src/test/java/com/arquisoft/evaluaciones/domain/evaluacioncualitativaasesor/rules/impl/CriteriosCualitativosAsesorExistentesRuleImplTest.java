package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CriteriosCualitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriteriosCualitativosAsesorExistentesRuleImplTest {

    private final CriteriosCualitativosAsesorExistentesRuleImpl regla = new CriteriosCualitativosAsesorExistentesRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodosLosSolicitadosExisten() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaCriteriosCualitativosAsesor(Set.of(id), Set.of(id, UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoFaltaAlgunoDeLosSolicitados() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var faltante = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaCriteriosCualitativosAsesor(Set.of(existente, faltante), Set.of(existente));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(CriteriosCualitativosAsesorNoEncontradosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIOS_NO_ENCONTRADOS);
                    assertThat(exception.getMessage()).contains(faltante.toString());
                });
    }
}
