package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgaPaquetesAnterioresDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var purga = PurgaPaquetesAnterioresDomain.crear(proyecto, 2);

        // Assert
        assertThat(purga.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(purga.getVersionEntregable()).isEqualTo(2);
    }

    @Test
    void debeAcumularErrores_cuandoFaltaElProyectoYLaVersionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> PurgaPaquetesAnterioresDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                                ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE));
    }

    @Test
    void debeFallar_cuandoLaVersionEsMenorAUno() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> PurgaPaquetesAnterioresDomain.crear(proyecto, 0))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.campo())
                                .isEqualTo(ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE)));
    }
}
