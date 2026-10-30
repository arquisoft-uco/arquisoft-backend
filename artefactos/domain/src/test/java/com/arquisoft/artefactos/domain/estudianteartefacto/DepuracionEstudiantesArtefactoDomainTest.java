package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepuracionEstudiantesArtefactoDomainTest {

    @Test
    void debeCrear_cuandoElArtefactoEsValido() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();

        // Act
        var depuracion = DepuracionEstudiantesArtefactoDomain.crear(artefacto);

        // Assert
        assertThat(depuracion.getArtefacto()).isEqualTo(artefacto);
    }

    @Test
    void debeLanzarValidacion_cuandoElArtefactoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> DepuracionEstudiantesArtefactoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactly(ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO));
    }
}
