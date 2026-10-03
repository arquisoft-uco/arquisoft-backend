package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEstadosFichaPerfilAsesorQueryTest {

    @Test
    void debeCrearQuery_cuandoAsesorFichaValido() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarEstadosFichaPerfilAsesorQuery.crear(asesorFicha, criterio);

        // Assert
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoAsesorFichaEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaPerfilAsesorQuery.crear(null, criterio));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstadoFichaPerfil.ASESOR_FICHA);
                    assertThat(e.codigoError())
                            .isEqualTo(FichasCodes.EstadoFichaPerfil.ASESOR_FICHA_ID_REQUERIDO);
                });
    }
}
