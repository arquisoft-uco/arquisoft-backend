package com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate;

import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionFichaPerfilAggregateTest {

    @Test
    void debeConstruirEntidad_cuandoDatosValidos() {
        // Arrange
        UUID representanteComiteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EvaluacionFichaPerfilAggregate evaluacion = EvaluacionFichaPerfilAggregate.crear(
                representanteComiteId,
                fichaPerfilId);

        // Assert
        assertThat(evaluacion).isNotNull();
        assertThat(evaluacion.getId()).isNotNull();
        assertThat(evaluacion.getRepresentanteComiteId()).isEqualTo(representanteComiteId);
        assertThat(evaluacion.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(evaluacion.getFechaCreacion()).isNotNull();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID representanteComiteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        var fechaCreacion = java.time.Instant.now();

        // Act
        EvaluacionFichaPerfilAggregate evaluacion = EvaluacionFichaPerfilAggregate.reconstruir(
                id,
                representanteComiteId,
                fichaPerfilId,
                fechaCreacion);

        // Assert
        assertThat(evaluacion).isNotNull();
        assertThat(evaluacion.getId()).isEqualTo(id);
        assertThat(evaluacion.getRepresentanteComiteId()).isEqualTo(representanteComiteId);
        assertThat(evaluacion.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(evaluacion.getFechaCreacion()).isEqualTo(fechaCreacion);
    }

    @Test
    void debeLanzarExcepcion_cuandoRepresentanteComiteIdEsNulo() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> EvaluacionFichaPerfilAggregate.crear(null, fichaPerfilId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasMessages.EvaluacionFichaPerfil.CAMPO_REPRESENTANTE_COMITE_ID);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaPerfilIdEsNulo() {
        // Arrange
        UUID representanteComiteId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> EvaluacionFichaPerfilAggregate.crear(representanteComiteId, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasMessages.EvaluacionFichaPerfil.CAMPO_FICHA_PERFIL_ID);
    }
}
