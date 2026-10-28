package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarArtefactosEntregableQueryTest {

    @Test
    void debeCrear_cuandoElEntregableEsValido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarArtefactosEntregableQuery.crear(entregable);

        // Assert
        assertThat(query.entregableProyectoGrado()).isEqualTo(entregable);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltaElEntregable() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarArtefactosEntregableQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult()
                                .tieneErroresDeCampo(EntregablesFields.EntregableProyectoGrado.ID)).isTrue());
    }
}
