package com.arquisoft.fichas.application.fichaperfil.query.primaryport.model;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarFichasPerfilAsesoradasQueryTest {

    @Test
    void debeCrearQuery_cuandoAsesorFichaValido() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarFichasPerfilAsesoradasQuery.crear(asesorFicha, criterio);

        // Assert
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoAsesorFichaEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarFichasPerfilAsesoradasQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
