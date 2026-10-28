package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactoAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorPropietarioArtefactoRuleImplTest {

    private final AsesorPropietarioArtefactoRuleImpl rule = new AsesorPropietarioArtefactoRuleImpl();

    @Test
    void debePasar_cuandoElAsesorEsDuenoDelArtefacto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var propio = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesor);

        // Act & Assert
        assertThatCode(() -> rule.validar(new PropiedadArtefactoAsesor(asesor, propio)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoElArtefactoEsDeOtroAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var ajeno = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new PropiedadArtefactoAsesor(asesor, ajeno)))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class)
                .satisfies(ex -> {
                    var excepcion = (AsesorNoPropietarioArtefactoException) ex;
                    assertThat(excepcion.getCodigoError()).isEqualTo(ArtefactosCodes.Artefacto.ASESOR_NO_PROPIETARIO);
                    assertThat(excepcion.getError().getMessage())
                            .doesNotContain(asesor.toString())
                            .doesNotContain(ajeno.getId().toString());
                });
    }
}
