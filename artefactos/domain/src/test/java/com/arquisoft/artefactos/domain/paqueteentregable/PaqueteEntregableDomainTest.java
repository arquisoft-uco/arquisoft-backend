package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteEntregableDomainTest {

    private static GeneracionPaqueteEntregableDomain generacion() {
        return GeneracionPaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 3);
    }

    @Test
    void debeCrearConIdYFechaDeLaGeneracion_cuandoDatosValidos() {
        // Arrange
        var generacion = generacion();

        // Act
        var paquete = PaqueteEntregableDomain.crear(generacion, " paquetes/x.zip ", 100L);

        // Assert
        assertThat(paquete.getId()).isNotNull();
        assertThat(paquete.getEntregableProyectoGrado()).isEqualTo(generacion.getEntregableProyectoGrado());
        assertThat(paquete.getProyectoGrado()).isEqualTo(generacion.getProyectoGrado());
        assertThat(paquete.getVersionEntregable()).isEqualTo(3);
        assertThat(paquete.getGeneradoEn()).isEqualTo(generacion.getGeneradoEn());
        assertThat(paquete.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(paquete.getTamanoBytes()).isEqualTo(100L);
    }

    @Test
    void debeAcumularErrores_cuandoClaveVaciaYTamanoCero() {
        // Arrange
        var generacion = generacion();

        // Act & Assert
        assertThatThrownBy(() -> PaqueteEntregableDomain.crear(generacion, " ", 0L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.PaqueteEntregable.CLAVE_OBJETO);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.PaqueteEntregable.CLAVE_OBJETO_REQUERIDA);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.PaqueteEntregable.TAMANO_BYTES);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO);
                    });
                });
    }

    @Test
    void debeRechazarSinNullPointer_cuandoLaGeneracionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> PaqueteEntregableDomain.crear(null, "paquetes/x.zip", 5L))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> {
                            assertThat(error.campo()).isEqualTo(ArtefactosFields.PaqueteEntregable.GENERACION);
                            assertThat(error.codigoError())
                                    .isEqualTo(ArtefactosCodes.PaqueteEntregable.GENERACION_REQUERIDA);
                        }));
    }
}
