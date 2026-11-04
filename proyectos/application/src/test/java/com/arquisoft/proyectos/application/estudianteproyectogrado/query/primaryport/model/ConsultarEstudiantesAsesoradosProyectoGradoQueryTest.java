package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesAsesoradosProyectoGradoQueryTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstudiantesAsesoradosProyectoGradoQuery.crear(proyectoGrado, asesor);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoProyectoYAsesorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesAsesoradosProyectoGradoQuery.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.ASESOR_ID_REQUERIDO));
    }
}
