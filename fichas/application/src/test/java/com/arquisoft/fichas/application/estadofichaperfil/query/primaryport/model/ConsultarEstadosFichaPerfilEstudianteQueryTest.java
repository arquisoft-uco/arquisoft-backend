package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEstadosFichaPerfilEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosUuidPresentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarEstadosFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidation_cuandoFichaPerfilNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaPerfilEstudianteQuery.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                });
    }

    @Test
    void debeLanzarApplicationValidation_cuandoEstudianteNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaPerfilEstudianteQuery.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ESTUDIANTE);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.EstadoFichaPerfil.ESTUDIANTE_ID_REQUERIDO);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoAmbosNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaPerfilEstudianteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO,
                        FichasCodes.EstadoFichaPerfil.ESTUDIANTE_ID_REQUERIDO);
    }
}
