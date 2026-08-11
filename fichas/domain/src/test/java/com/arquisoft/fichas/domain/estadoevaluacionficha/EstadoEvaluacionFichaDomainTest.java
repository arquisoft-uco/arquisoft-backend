package com.arquisoft.fichas.domain.estadoevaluacionficha;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionFichaDomainTest {

    @Test
    void debeConstruirEntidadAutomatica_cuandoFactoryCrear() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();

        // Act
        EstadoEvaluacionFichaDomain aggregate = EstadoEvaluacionFichaDomain.crear(evaluacionId);

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isNotNull();
        assertThat(aggregate.getEvaluacionFichaPerfilId()).isEqualTo(evaluacionId);
        assertThat(aggregate.getEstadoEvaluacion()).isEqualTo(EstadoEvaluacion.EN_EVALUACION);
        assertThat(aggregate.getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeConstruirEntidadManual_cuandoFactoryCrearConEstado() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        EstadoEvaluacion estadoEvaluacion = EstadoEvaluacion.APROBADA;
        EstadoEvaluacion ultimoEstado = EstadoEvaluacion.EN_EVALUACION;

        // Act
        EstadoEvaluacionFichaDomain aggregate = EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacionId,
                estadoEvaluacion,
                ultimoEstado);

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isNotNull();
        assertThat(aggregate.getEvaluacionFichaPerfilId()).isEqualTo(evaluacionId);
        assertThat(aggregate.getEstadoEvaluacion()).isEqualTo(EstadoEvaluacion.APROBADA);
        assertThat(aggregate.getFechaActualizacion()).isNotNull();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionId = null;
        EstadoEvaluacion estadoEvaluacion = null;
        var fechaActualizacion = java.time.Instant.now();

        // Act
        EstadoEvaluacionFichaDomain aggregate = EstadoEvaluacionFichaDomain.reconstruir(
                id,
                evaluacionId,
                estadoEvaluacion,
                fechaActualizacion);

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getEvaluacionFichaPerfilId()).isNull();
        assertThat(aggregate.getEstadoEvaluacion()).isNull();
    }

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionIdEsNulEnFactoryCrear() {
        // Arrange
        UUID evaluacionId = null;

        // Act & Assert
        assertThatThrownBy(() -> EstadoEvaluacionFichaDomain.crear(evaluacionId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("no puede ser nulo");
    }

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionIdEsNulEnFactoryCrearConEstado() {
        // Arrange
        UUID evaluacionId = null;
        EstadoEvaluacion estadoEvaluacion = EstadoEvaluacion.APROBADA;
        EstadoEvaluacion ultimoEstado = EstadoEvaluacion.EN_EVALUACION;

        // Act & Assert
        assertThatThrownBy(() -> EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacionId,
                estadoEvaluacion,
                ultimoEstado))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("no puede ser nulo");
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoIdEsNulEnFactoryCrearConEstado() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        EstadoEvaluacion estadoEvaluacion = null;
        EstadoEvaluacion ultimoEstado = EstadoEvaluacion.EN_EVALUACION;

        // Act & Assert
        assertThatThrownBy(() -> EstadoEvaluacionFichaDomain.crearConEstado(
                evaluacionId,
                estadoEvaluacion,
                ultimoEstado))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("no puede ser nulo");
    }
}
