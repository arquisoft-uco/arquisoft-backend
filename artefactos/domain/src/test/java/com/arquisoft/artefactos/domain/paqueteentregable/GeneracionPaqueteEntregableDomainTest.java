package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneracionPaqueteEntregableDomainTest {

    @Test
    void debeCrearConFechaGenerada_cuandoDatosValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var antes = Instant.now();

        // Act
        var generacion = GeneracionPaqueteEntregableDomain.crear(entregable, proyecto, 2);

        // Assert
        assertThat(generacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(generacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(generacion.getVersionEntregable()).isEqualTo(2);
        assertThat(generacion.getGeneradoEn()).isBetween(antes, Instant.now());
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> GeneracionPaqueteEntregableDomain.crear(null, null, 0))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).contains(
                            ArtefactosFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                            ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                            ArtefactosFields.PaqueteEntregable.VERSION_ENTREGABLE);
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(ArtefactosCodes.PaqueteEntregable.VERSION_ENTREGABLE_INVALIDA));
                });
    }
}
