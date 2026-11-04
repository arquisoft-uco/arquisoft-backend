package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarItemsFichaPerfilCoordinadorQueryTest {

    @Test
    void debeCrearQuery_cuandoFichaPerfilValida() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarItemsFichaPerfilCoordinadorQuery.crear(fichaPerfil);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
    }

    @Test
    void debeLanzarValidacion_cuandoFichaPerfilEsNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarItemsFichaPerfilCoordinadorQuery.crear(null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.ItemFichaPerfil.FICHA_PERFIL);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO);
                });
    }
}
