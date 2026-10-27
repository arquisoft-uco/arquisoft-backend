package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactosAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorPropietarioArtefactosRuleImplTest {

    private final AsesorPropietarioArtefactosRuleImpl rule = new AsesorPropietarioArtefactosRuleImpl();

    @Test
    void debePasar_cuandoElAsesorEsDuenoDeTodosLosArtefactos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var propio = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesor);

        // Act & Assert
        assertThatCode(() -> rule.validar(new PropiedadArtefactosAsesor(asesor, List.of(propio))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoUnArtefactoEsDeOtroAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var ajeno = ArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new PropiedadArtefactosAsesor(asesor, List.of(ajeno))))
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
