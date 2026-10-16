package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarEstadosFichaPerfilCoordinadorQueryTest {

    @Test
    void debeCrearQuery_cuandoFichaEsValida() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstadosFichaPerfilCoordinadorQuery.crear(fichaPerfil);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
    }

    @Test
    void debeLanzarValidacion_cuandoFichaEsNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaPerfilCoordinadorQuery.crear(null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(e -> e.campo(), e -> e.codigoError())
                .containsExactly(tuple(FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                        FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO));
    }
}
