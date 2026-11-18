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
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(entregable, "  CERRADO ", asesor);

        // Assert
        assertThat(cambio.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(cambio.getEstadoEntregable()).isEqualTo(EstadoEntregable.CERRADO);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanAsesorEntregableYEstado() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEntregableProyectoGradoDomain.crear(null, "  ", null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO,
                                        EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO,
                                        EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO));
    }

    @Test
    void debeAgregarErrorDeCampo_cuandoElEstadoNoExisteEnElCatalogo() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEntregableProyectoGradoDomain.crear(entregable, "INEXISTENTE", UtilUUID.generarNuevoUUID()))
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
        assertThatThrownBy(() -> CambioEstadoEntregableProyectoGradoDomain.crear(entregable, "VACIO", UtilUUID.generarNuevoUUID()))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactly(EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO));
    }
}
