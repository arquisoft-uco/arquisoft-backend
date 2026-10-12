package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarEstadosFichaPerfilRepresentanteQueryTest {

    @Test
    void debeCrearQuery_cuandoAmbosIdsPresentes() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstadosFichaPerfilRepresentanteQuery.crear(fichaPerfil, representanteComite);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(query.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoAmbosIdsNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaPerfilRepresentanteQuery.crear(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(e -> e.campo(), e -> e.codigoError())
                .containsExactlyInAnyOrder(
                        tuple(FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO),
                        tuple(FichasFields.EstadoFichaPerfil.REPRESENTANTE_COMITE,
                                FichasCodes.EstadoFichaPerfil.REPRESENTANTE_COMITE_ID_REQUERIDO));
    }
}
