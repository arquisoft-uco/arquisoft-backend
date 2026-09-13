package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarCompanerosFichaPerfilQueryTest {

    @Test
    void debeCrearQuery_cuandoFichaPerfilYEstudianteNoSonNulos() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarCompanerosFichaPerfilQuery.crear(fichaPerfil, estudiante);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoFichaPerfilEsNulo() {
        // Arrange
        var estudiante = UUID.randomUUID();

        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarCompanerosFichaPerfilQuery.crear(null, estudiante));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstudianteFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                });
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEstudianteEsNulo() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();

        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarCompanerosFichaPerfilQuery.crear(fichaPerfil, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstudianteFichaPerfil.ESTUDIANTE);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoFichaPerfilYEstudianteSonNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarCompanerosFichaPerfilQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO,
                        FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_ID_REQUERIDO);
    }
}
