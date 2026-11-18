package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarCoordinadoresProyectoGradoEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoEstudianteValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarCoordinadoresProyectoGradoEstudianteQuery.crear(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarValidacion400_cuandoEstudianteNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarCoordinadoresProyectoGradoEstudianteQuery.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactly(ProyectosCodes.ProyectoGrado.ESTUDIANTE_ID_REQUERIDO));
    }
}
