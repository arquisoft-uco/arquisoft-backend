package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEvaluacionesFichaPerfilRepresentanteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosIdentificadoresNoNulos() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();

        // Act
        var query = ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeLanzar400_cuandoFichaEsNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EvaluacionFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA);
                });
    }

    @Test
    void debeLanzar400_cuandoRepresentanteEsNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EvaluacionFichaPerfil.REPRESENTANTE_COMITE);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoFichaYRepresentanteNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEvaluacionesFichaPerfilRepresentanteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        FichasCodes.EvaluacionFichaPerfil.FICHA_REQUERIDA,
                        FichasCodes.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO);
    }
}
