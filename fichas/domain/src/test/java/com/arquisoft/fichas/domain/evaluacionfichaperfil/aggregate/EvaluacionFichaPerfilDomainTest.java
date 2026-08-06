package com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate;

import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionFichaPerfilDomainTest {

    @Test
    void debeConstruirEntidad_cuandoDatosValidos() {
        // Arrange
        UUID representanteComiteId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();

        // Act
        EvaluacionFichaPerfilDomain evaluacion = EvaluacionFichaPerfilDomain.crear(
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
        EvaluacionFichaPerfilDomain evaluacion = EvaluacionFichaPerfilDomain.reconstruir(
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
        assertThatThrownBy(() -> EvaluacionFichaPerfilDomain.crear(null, fichaPerfilId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.EvaluacionFichaPerfil.REPRESENTANTE_COMITE);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaPerfilIdEsNulo() {
        // Arrange
        UUID representanteComiteId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> EvaluacionFichaPerfilDomain.crear(representanteComiteId, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL);
    }
}
