package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepuracionEstudiantesArtefactoDomainTest {

    @Test
    void debeCrear_cuandoElArtefactoYLosConservadosSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var conservado = UtilUUID.generarNuevoUUID();

        // Act
        var depuracion = DepuracionEstudiantesArtefactoDomain.crear(artefacto, List.of(conservado));

        // Assert
        assertThat(depuracion.getArtefacto()).isEqualTo(artefacto);
        assertThat(depuracion.getConservar()).containsExactly(conservado);
    }

    @Test
    void debeAcumularErrores_cuandoElArtefactoEsNuloYNoHayConservados() {
        // Act & Assert
        assertThatThrownBy(() -> DepuracionEstudiantesArtefactoDomain.crear(null, List.of()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO,
                                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS));
    }
}
