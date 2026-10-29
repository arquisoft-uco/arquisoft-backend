package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneracionEntregableProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var generacion = GeneracionEntregableProyectoGradoDomain.crear(proyecto, asesor);

        // Assert
        assertThat(generacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(generacion.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanProyectoYAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> GeneracionEntregableProyectoGradoDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> assertThat(ex.getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO));
    }
}
