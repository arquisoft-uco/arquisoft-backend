package com.arquisoft.fichas.application.fichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarFichasPerfilEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarFichasPerfilEstudianteQuery.crear(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEstudianteNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarFichasPerfilEstudianteQuery.crear(null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting("codigoError")
                .containsExactly(FichasCodes.FichaPerfil.ESTUDIANTE_REQUERIDO);
    }
}
