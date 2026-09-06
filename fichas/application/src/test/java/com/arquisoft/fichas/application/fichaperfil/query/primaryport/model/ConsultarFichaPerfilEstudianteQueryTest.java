package com.arquisoft.fichas.application.fichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarFichaPerfilEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidationException_conAmbosFieldErrors_cuandoFichaPerfilYEstudianteNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarFichaPerfilEstudianteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting("codigoError")
                .containsExactlyInAnyOrder(
                        FichasCodes.FichaPerfil.ID_REQUERIDO,
                        FichasCodes.FichaPerfil.ESTUDIANTE_REQUERIDO);
    }
}
