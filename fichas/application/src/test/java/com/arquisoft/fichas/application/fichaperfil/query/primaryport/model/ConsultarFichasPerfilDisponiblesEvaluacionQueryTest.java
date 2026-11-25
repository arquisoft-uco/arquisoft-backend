package com.arquisoft.fichas.application.fichaperfil.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarFichasPerfilDisponiblesEvaluacionQueryTest {

    @Test
    void debeCrearQuery_cuandoRepresentanteInformado() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarFichasPerfilDisponiblesEvaluacionQuery.crear(representante, criterio);

        // Assert
        assertThat(query.representanteComite()).isEqualTo(representante);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoRepresentanteEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarFichasPerfilDisponiblesEvaluacionQuery.crear(null, criterio));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .extracting(e -> e.campo(), e -> e.codigoError())
                .containsExactly(tuple(FichasFields.FichaPerfil.REPRESENTANTE_COMITE,
                        FichasCodes.FichaPerfil.REPRESENTANTE_REQUERIDO));
    }
}
