package com.arquisoft.entregables.domain.asesor.rules.impl;

import com.arquisoft.entregables.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorVigenteRuleImplTest {

    private final AsesorVigenteRuleImpl regla = new AsesorVigenteRuleImpl();

    @Test
    void debeLanzarSinExponerElId_cuandoElAsesorNoEstaVigente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesor(asesor, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOfSatisfying(AsesorNoVigenteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.Asesor.NO_VIGENTE);
                    assertThat(ex.getError().getMessage()).doesNotContain(asesor.toString());
                    assertThat(ex.getMessage()).contains(asesor.toString());
                });
    }

    @Test
    void debePasar_cuandoElAsesorEstaVigente() {
        // Arrange
        var vigencia = new VigenciaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
