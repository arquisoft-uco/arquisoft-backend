package com.arquisoft.entregables.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteEntregableDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var generadoEn = Instant.now();

        // Act
        var paquete = PaqueteEntregableDomain.crear(
                id, entregable, "bucket", "paquetes/x.zip", "p.zip", 10L, generadoEn);

        // Assert
        assertThat(paquete.getId()).isEqualTo(id);
        assertThat(paquete.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(paquete.getBucket()).isEqualTo("bucket");
        assertThat(paquete.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(paquete.getNombreArchivo()).isEqualTo("p.zip");
        assertThat(paquete.getTamanoBytes()).isEqualTo(10L);
        assertThat(paquete.getGeneradoEn()).isEqualTo(generadoEn);
        assertThat(paquete.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErroresDeCampo_cuandoFaltanDatosYTamanoEsCero() {
        // Act & Assert
        assertThatThrownBy(() -> PaqueteEntregableDomain.crear(null, null, " ", null, "", 0L, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).contains(
                            EntregablesFields.PaqueteEntregable.ID,
                            EntregablesFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO,
                            EntregablesFields.PaqueteEntregable.BUCKET,
                            EntregablesFields.PaqueteEntregable.CLAVE_OBJETO,
                            EntregablesFields.PaqueteEntregable.NOMBRE_ARCHIVO,
                            EntregablesFields.PaqueteEntregable.TAMANO_BYTES,
                            EntregablesFields.PaqueteEntregable.GENERADO_EN);
                    assertThat(errores).anySatisfy(error -> assertThat(error.codigoError())
                            .isEqualTo(EntregablesCodes.PaqueteEntregable.TAMANO_BYTES_INVALIDO));
                });
    }

    @Test
    void debeReconstruirYSerVacio_cuandoSeUsaElCentinela() {
        // Act
        var paquete = PaqueteEntregableDomain.reconstruir(null, null, null, null, null, 0L, null);

        // Assert
        assertThat(paquete.getId()).isNull();
        assertThat(paquete.esVacio()).isFalse();
        assertThat(PaqueteEntregableDomain.VACIO.esVacio()).isTrue();
    }
}
