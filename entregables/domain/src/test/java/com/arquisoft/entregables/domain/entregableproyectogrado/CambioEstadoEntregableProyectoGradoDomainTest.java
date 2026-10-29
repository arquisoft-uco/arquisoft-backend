package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoEntregableProyectoGradoDomainTest {

    @Test
    void debeCrearRecortandoElEstado_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(entregable, "  CERRADO ");

        // Assert
        assertThat(cambio.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(cambio.getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanEntregableYEstado() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEntregableProyectoGradoDomain.crear(null, "  "))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO,
                                        EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO));
    }

    @Test
    void debeAgregarErrorDeCampo_cuandoElEstadoNoExisteEnElCatalogo() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEntregableProyectoGradoDomain.crear(entregable, "INEXISTENTE"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> {
                    assertThat(ex.getValidationResult().getErrores())
                            .extracting("codigoError")
                            .containsExactly(EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO);
                    assertThat(ex.getValidationResult().getErrores())
                            .extracting("campo")
                            .containsExactly(EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE);
                });
    }

    @Test
    void debeRechazar_cuandoElEstadoEsElCentinelaVacio() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEntregableProyectoGradoDomain.crear(entregable, "VACIO"))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactly(EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO));
    }
}
