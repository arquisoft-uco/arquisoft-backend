package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarItemsFichaPerfilEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosUuidPresentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarItemsFichaPerfilEstudianteQuery.crear(fichaPerfil, estudiante);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidation_cuandoFichaPerfilNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilEstudianteQuery.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.ItemFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                });
    }

    @Test
    void debeLanzarApplicationValidation_cuandoEstudianteNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilEstudianteQuery.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.ItemFichaPerfil.ESTUDIANTE);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoAmbosNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilEstudianteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO,
                        FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO);
    }
}
