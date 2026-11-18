package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarHistorialEstadosProyectoGradoQueryTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarHistorialEstadosProyectoGradoQuery.crear(proyectoGrado, usuario);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeAcumularErrores_cuandoAmbosNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarHistorialEstadosProyectoGradoQuery.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstadoProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.EstadoProyectoGrado.USUARIO_ID_REQUERIDO));
    }
}
