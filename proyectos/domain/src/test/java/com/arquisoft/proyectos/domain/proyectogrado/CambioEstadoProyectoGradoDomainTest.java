package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoProyectoGradoDomainTest {

    @Test
    void debeCrearConElEstadoConvertido_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoProyectoGradoDomain.crear(proyectoGrado, asesor, "FINALIZADO");

        // Assert
        assertThat(cambio.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getEstadoNuevo()).isEqualTo(EstadoProyecto.FINALIZADO);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatosYElEstadoEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoProyectoGradoDomain.crear(null, null, " "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.ASESOR_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO));
        assertThatThrownBy(() -> CambioEstadoProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "INEXISTENTE"))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.ESTADO_PROYECTO_INVALIDO)));
    }
}
