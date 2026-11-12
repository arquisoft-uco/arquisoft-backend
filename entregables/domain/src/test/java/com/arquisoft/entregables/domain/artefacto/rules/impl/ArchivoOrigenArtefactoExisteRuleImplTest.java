package com.arquisoft.entregables.domain.artefacto.rules.impl;

import com.arquisoft.entregables.domain.artefacto.exception.ArchivoOrigenArtefactoNoDisponibleException;
import com.arquisoft.entregables.domain.artefacto.model.ExistenciaArchivoOrigenArtefacto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchivoOrigenArtefactoExisteRuleImplTest {

    private final ArchivoOrigenArtefactoExisteRuleImpl rule = new ArchivoOrigenArtefactoExisteRuleImpl();

    @Test
    void debePermitir_cuandoElArchivoExiste() {
        // Arrange
        var entrada = new ExistenciaArchivoOrigenArtefacto(UtilUUID.generarNuevoUUID(), "Doc", "clave", true);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinIdsEnElMensaje_cuandoElArchivoNoExiste() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaArchivoOrigenArtefacto(artefacto, "Doc", "origen/clave", false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(ArchivoOrigenArtefactoNoDisponibleException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.Artefacto.ARCHIVO_ORIGEN_NO_DISPONIBLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(artefacto.toString());
                });
    }
}
