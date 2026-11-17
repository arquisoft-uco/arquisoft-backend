package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizacionEstadoProyectoGradoDomainTest {

    @Test
    void debeCrearConEstadoRecortado_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");

        // Act
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(proyectoGrado, " ATRASADO ", ocurridoEn);

        // Assert
        assertThat(actualizacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(actualizacion.getEstadoProyecto()).isEqualTo(EstadoProyecto.ATRASADO);
        assertThat(actualizacion.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoIdNuloEstadoEnBlancoYFechaNula() {
        // Act & Assert
        assertThatThrownBy(() -> ActualizacionEstadoProyectoGradoDomain.crear(null, " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.ProyectoGrado.ID,
                                ArtefactosFields.ProyectoGrado.ESTADO_PROYECTO,
                                ArtefactosFields.ProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeRechazar_cuandoElEstadoNoEstaEnElCatalogo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");

        // Act & Assert
        assertThatThrownBy(() -> ActualizacionEstadoProyectoGradoDomain.crear(proyectoGrado, "INEXISTENTE", ocurridoEn))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.ProyectoGrado.ESTADO_PROYECTO_NO_ENCONTRADO)));
    }
}
