package com.arquisoft.biblioteca.application.documentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarDocumentacionQueryTest {

    @Test
    void debeCrearse_cuandoLosDatosSonValidos() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var query = DescargarDocumentacionQuery.crear(documentacion, List.of(RolConsultaDocumentacion.ESTUDIANTE));

        // Assert
        assertThat(query.documentacion()).isEqualTo(documentacion);
        assertThat(query.roles()).containsExactly(RolConsultaDocumentacion.ESTUDIANTE);
    }

    @Test
    void debeCrearse_cuandoLosRolesVienenVacios() {
        // Act
        var query = DescargarDocumentacionQuery.crear(UtilUUID.generarNuevoUUID(), List.of());

        // Assert
        assertThat(query.roles()).isEmpty();
    }

    @Test
    void debeAcumularAmbosErroresDeEntrada_cuandoDocumentacionYRolesSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> DescargarDocumentacionQuery.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO,
                                BibliotecaCodes.Documentacion.ROLES_REQUERIDOS));
    }
}
