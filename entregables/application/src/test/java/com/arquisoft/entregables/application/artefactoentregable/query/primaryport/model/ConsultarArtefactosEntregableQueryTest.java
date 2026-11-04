package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarArtefactosEntregableQueryTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarArtefactosEntregableQuery.crear(entregable, asesor);

        // Assert
        assertThat(query.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(query.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltanEntregableYAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarArtefactosEntregableQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.EntregableProyectoGrado.ID)).isTrue();
                    assertThat(ex.getValidationResult()
                            .tieneErroresDeCampo(EntregablesFields.EntregableProyectoGrado.ASESOR)).isTrue();
                });
    }
}
