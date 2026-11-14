package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelacionProyectoGradoDomainTest {

    @Test
    void debeCrearConEstadoCancelado_cuandoProyectoValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var cancelacion = CancelacionProyectoGradoDomain.crear(proyectoGrado);

        // Assert
        assertThat(cancelacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(cancelacion.getEstadoNuevo()).isEqualTo(EstadoProyecto.CANCELADO);
    }

    @Test
    void debeAcumularFieldError_cuandoElProyectoGradoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CancelacionProyectoGradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.ProyectoGrado.ID_REQUERIDO)));
    }
}
