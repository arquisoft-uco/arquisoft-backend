package com.arquisoft.evaluaciones.domain.entregable;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var entregable = EntregableDomain.crear(id, proyectoGrado, "Sistema de riego", 2);

        // Assert
        assertThat(entregable.getId()).isEqualTo(id);
        assertThat(entregable.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(entregable.getProyecto()).isEqualTo("Sistema de riego");
        assertThat(entregable.getVersion()).isEqualTo(2);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> EntregableDomain.crear(null, null, "  ", null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.Entregable.ENTREGABLE_REQUERIDO,
                                        EvaluacionesCodes.Entregable.PROYECTO_GRADO_REQUERIDO,
                                        EvaluacionesCodes.Entregable.PROYECTO_REQUERIDO,
                                        EvaluacionesCodes.Entregable.VERSION_REQUERIDA));
    }
}
