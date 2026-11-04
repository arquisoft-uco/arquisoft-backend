package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesProyectoGradoCoordinadorQueryTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstudiantesProyectoGradoCoordinadorQuery.crear(proyectoGrado, coordinador);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.coordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularErrores_cuandoProyectoYCoordinadorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesProyectoGradoCoordinadorQuery.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO));
    }
}
