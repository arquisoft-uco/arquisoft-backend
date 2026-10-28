package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteYaVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculosEstudiantesArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesNoVinculadosArtefactoRuleImplTest {

    private static final String NOMBRE = "Laura Gomez";

    private final EstudiantesNoVinculadosArtefactoRuleImpl rule = new EstudiantesNoVinculadosArtefactoRuleImpl();

    @Test
    void debePasar_cuandoNingunParEstaVinculado() {
        // Act & Assert
        assertThatCode(() -> rule.validar(new VinculosEstudiantesArtefacto(List.of(), List.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConNombreSinUuid_cuandoUnParYaEstaVinculado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var conocido = EstudianteDomain.reconstruir(estudiante, "1", NOMBRE, "l@uco.edu.co", Instant.EPOCH, null);
        var vinculos = new VinculosEstudiantesArtefacto(
                List.of(conocido), List.of(new ParEstudianteArtefacto(UtilUUID.generarNuevoUUID(), estudiante)));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vinculos))
                .isInstanceOf(EstudianteYaVinculadoArtefactoException.class)
                .satisfies(ex -> {
                    var excepcion = (EstudianteYaVinculadoArtefactoException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_YA_VINCULADO);
                    assertThat(excepcion.getError().getMessage()).contains(NOMBRE).doesNotContain(estudiante.toString());
                });
    }
}
