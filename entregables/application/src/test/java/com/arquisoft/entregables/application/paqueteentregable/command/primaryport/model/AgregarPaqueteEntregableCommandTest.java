package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarPaqueteEntregableCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var paquete = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var generadoEn = Instant.now();

        // Act
        var command = AgregarPaqueteEntregableCommand.crear(
                paquete.toString(), entregable.toString(), "bucket", "paquetes/x.zip", "p.zip", 10L, generadoEn);

        // Assert
        assertThat(command.paqueteEntregable()).isEqualTo(paquete);
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.bucket()).isEqualTo("bucket");
        assertThat(command.claveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(command.nombreArchivo()).isEqualTo("p.zip");
        assertThat(command.tamanoBytes()).isEqualTo(10L);
        assertThat(command.generadoEn()).isEqualTo(generadoEn);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoUuidTamanoOTextoInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarPaqueteEntregableCommand.crear("no-uuid", null, " ", "", null, 0L, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.PaqueteEntregable.ID)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.PaqueteEntregable.ENTREGABLE_PROYECTO_GRADO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.PaqueteEntregable.BUCKET)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.PaqueteEntregable.CLAVE_OBJETO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.PaqueteEntregable.NOMBRE_ARCHIVO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.PaqueteEntregable.TAMANO_BYTES)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.PaqueteEntregable.GENERADO_EN)).isTrue();
                });
    }
}
