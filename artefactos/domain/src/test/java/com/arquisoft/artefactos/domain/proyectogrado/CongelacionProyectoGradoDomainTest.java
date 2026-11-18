package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CongelacionProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var instante = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var accion = CongelacionProyectoGradoDomain.crear(proyecto, instante);

        // Assert
        assertThat(accion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(accion.getEntregadoEn()).isEqualTo(instante);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanProyectoYFecha() {
        // Act & Assert
        assertThatThrownBy(() -> CongelacionProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.ProyectoGrado.ID, ArtefactosFields.ProyectoGrado.ENTREGADO_EN));
    }
}
