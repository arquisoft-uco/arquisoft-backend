package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoNoOmitidoRuleImplTest {

    private final ArtefactoNoOmitidoRuleImpl rule = new ArtefactoNoOmitidoRuleImpl();

    @Test
    void debePasar_cuandoElArtefactoEstaActivo() {
        // Arrange
        var artefacto = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> rule.validar(new ActividadArtefacto(artefacto))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoOmitidoSinExponerElId_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var artefacto = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        artefacto.omitir(UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ActividadArtefacto(artefacto)))
                .isInstanceOf(ArtefactoOmitidoException.class)
                .satisfies(ex -> {
                    var omitido = (ArtefactoOmitidoException) ex;
                    assertThat(omitido.getCodigoError()).isEqualTo(ArtefactosCodes.Artefacto.OMITIDO);
                    assertThat(omitido.getError().getMessage()).doesNotContain(artefacto.getId().toString());
                });
    }
}
