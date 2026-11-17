package com.arquisoft.evaluaciones.application.evaluacion.command.result.mapper;

import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneracionEvaluacionResultMapperTest {

    @Test
    void debeMapearGenerada() {
        // Arrange
        var evaluacion = EvaluacionDomain.crear(UtilUUID.generarNuevoUUID());

        // Act
        var resultado = GeneracionEvaluacionResultMapper.toResultGenerada(evaluacion);

        // Assert
        assertThat(resultado.evaluacion()).isEqualTo(evaluacion.getId());
        assertThat(resultado.entregable()).isEqualTo(evaluacion.getEntregable());
    }

    @Test
    void debeMapearDuplicada() {
        // Arrange
        var evaluacion = EvaluacionDomain.crear(UtilUUID.generarNuevoUUID());

        // Act
        var resultado = GeneracionEvaluacionResultMapper.toResultDuplicada(evaluacion);

        // Assert
        assertThat(resultado.entregable()).isEqualTo(evaluacion.getEntregable());
    }
}
