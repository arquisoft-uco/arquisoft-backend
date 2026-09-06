package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarItemsFichaPerfilRepresentanteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosUuidPresentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();

        // Act
        var query = ConsultarItemsFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeLanzarApplicationValidation_cuandoFichaPerfilNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilRepresentanteQuery.crear(null, UUID.randomUUID()));

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
    void debeLanzarApplicationValidation_cuandoRepresentanteComiteNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilRepresentanteQuery.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.ItemFichaPerfil.REPRESENTANTE_COMITE);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.ItemFichaPerfil.REPRESENTANTE_COMITE_REQUERIDO);
                });
    }

    @Test
    void debeAcumularAmbosErrores_cuandoAmbosNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilRepresentanteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(ValidationResult.ValidationError::codigoError)
                .containsExactlyInAnyOrder(
                        FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO,
                        FichasCodes.ItemFichaPerfil.REPRESENTANTE_COMITE_REQUERIDO);
    }
}
