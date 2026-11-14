package com.arquisoft.biblioteca.application.documentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarDocumentacionesQueryTest {

    @Test
    void debeCrearse_cuandoRolesYCriterioSonValidos() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarDocumentacionesQuery.crear(List.of(RolConsultaDocumentacion.ASESOR), criterio);

        // Assert
        assertThat(query.roles()).containsExactly(RolConsultaDocumentacion.ASESOR);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeCrearse_cuandoLosRolesVienenVacios() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarDocumentacionesQuery.crear(List.of(), criterio);

        // Assert
        assertThat(query.roles()).isEmpty();
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoRolesEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarDocumentacionesQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(BibliotecaCodes.Documentacion.ROLES_REQUERIDOS));
    }
}
