package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculoEstudianteArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteVinculadoArtefactoRuleImplTest {

    private static final String NOMBRE = "Laura Gomez";

    private final EstudianteVinculadoArtefactoRuleImpl rule = new EstudianteVinculadoArtefactoRuleImpl();

    @Test
    void debePasar_cuandoElEstudianteEstaVinculado() {
        // Arrange
        var vinculo = new VinculoEstudianteArtefacto(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), NOMBRE, true);

        // Act & Assert
        assertThatCode(() -> rule.validar(vinculo)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConNombreSinUuid_cuandoElEstudianteNoEstaVinculado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var vinculo = new VinculoEstudianteArtefacto(estudiante, artefacto, NOMBRE, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vinculo))
                .isInstanceOf(EstudianteNoVinculadoArtefactoException.class)
                .satisfies(ex -> {
                    var excepcion = (EstudianteNoVinculadoArtefactoException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_NO_VINCULADO_ARTEFACTO);
                    assertThat(excepcion.getError().getMessage())
                            .contains(NOMBRE)
                            .doesNotContain(estudiante.toString())
                            .doesNotContain(artefacto.toString());
                });
    }
}
