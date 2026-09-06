package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEstudiantesFichaPerfilQueryTest {

    @Test
    void debeCrearQuery_cuandoFichaPerfilNoEsNulo() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();

        // Act
        var query = ConsultarEstudiantesFichaPerfilQuery.crear(fichaPerfil);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoFichaPerfilEsNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstudiantesFichaPerfilQuery.crear(null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstudianteFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.EstudianteFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                });
    }
}
