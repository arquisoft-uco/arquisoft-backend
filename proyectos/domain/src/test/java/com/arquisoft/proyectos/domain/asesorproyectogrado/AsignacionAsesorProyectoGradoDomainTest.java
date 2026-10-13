package com.arquisoft.proyectos.domain.asesorproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignacionAsesorProyectoGradoDomainTest {

    @Test
    void debeExponerComponentes_cuandoComponentesValidos() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var asignacion = AsignacionAsesorProyectoGradoDomain.crear(vinculo, coordinador);

        // Assert
        assertThat(asignacion.getAsesorProyectoGrado()).isSameAs(vinculo);
        assertThat(asignacion.getCoordinador()).isEqualTo(coordinador);
        assertThat(asignacion.getAsesor()).isEqualTo(vinculo.getAsesor());
        assertThat(asignacion.getProyectoGrado()).isEqualTo(vinculo.getProyectoGrado());
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanComponentes() {
        // Act & Assert
        assertThatThrownBy(() -> AsignacionAsesorProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO));
    }
}
