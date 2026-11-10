package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

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
                paquete.toString(), entregable.toString(),
                new DatosArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 10L), generadoEn, null);

        // Assert
        assertThat(command.paqueteEntregable()).isEqualTo(paquete);
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.bucket()).isEqualTo("bucket");
        assertThat(command.claveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(command.nombreArchivo()).isEqualTo("p.zip");
        assertThat(command.tamanoBytes()).isEqualTo(10L);
        assertThat(command.generadoEn()).isEqualTo(generadoEn);
    }

    private static DatosArtefactoPaquete item(String artefacto, String nombreDocumento, String ruta) {
        return new DatosArtefactoPaquete(artefacto, 3, UtilUUID.generarNuevoUUID().toString(), nombreDocumento,
                UtilUUID.generarNuevoUUID().toString(), "Repo", ruta);
    }

    @Test
    void debeCrear_cuandoHayArtefactosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var datos = item(artefacto.toString(), "Informe", "01-Informe/v3-informe.pdf");

        // Act
        var command = AgregarPaqueteEntregableCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(),
                new DatosArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 10L), Instant.now(), List.of(datos));

        // Assert
        assertThat(command.artefactos()).singleElement().satisfies(creado -> {
            assertThat(creado.artefacto()).isEqualTo(artefacto);
            assertThat(creado.numeroVersion()).isEqualTo(3);
            assertThat(creado.versionRepositorioArtefacto()).hasToString(datos.versionRepositorioArtefacto());
            assertThat(creado.repositorio()).hasToString(datos.repositorio());
            assertThat(creado.nombreDocumento()).isEqualTo("Informe");
            assertThat(creado.nombreRepositorio()).isEqualTo("Repo");
            assertThat(creado.rutaEnPaquete()).isEqualTo("01-Informe/v3-informe.pdf");
        });
    }

    @Test
    void debeTratarComoVacia_cuandoArtefactosEsNulo() {
        // Act
        var command = AgregarPaqueteEntregableCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(),
                new DatosArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 10L), Instant.now(), null);

        // Assert
        assertThat(command.artefactos()).isEmpty();
    }

    @Test
    void debeAcumularErrores_cuandoUnItemEstaMalformado() {
        // Arrange
        var malformado = new DatosArtefactoPaquete("no-uuid", 0, null, " ", "tampoco", "x".repeat(101),
                "r".repeat(2001));

        // Act & Assert
        assertThatThrownBy(() -> AgregarPaqueteEntregableCommand.crear(
                UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(),
                new DatosArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 10L), Instant.now(), List.of(malformado)))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.ARTEFACTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NUMERO_VERSION)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.VERSION_REPOSITORIO_ARTEFACTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.REPOSITORIO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NOMBRE_DOCUMENTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NOMBRE_REPOSITORIO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.URI_ARCHIVO)).isTrue();
                });
    }

    @Test
    void debeAcumularError_cuandoNumeroVersionEsNuloOCero() {
        // Arrange
        var sinVersion = new DatosArtefactoPaquete(UtilUUID.generarNuevoUUID().toString(), null,
                UtilUUID.generarNuevoUUID().toString(), "Informe", UtilUUID.generarNuevoUUID().toString(), "Repo",
                "ruta/a.pdf");
        var enCero = new DatosArtefactoPaquete(UtilUUID.generarNuevoUUID().toString(), 0,
                UtilUUID.generarNuevoUUID().toString(), "Informe", UtilUUID.generarNuevoUUID().toString(), "Repo",
                "ruta/a.pdf");

        // Act & Assert
        for (var datos : List.of(sinVersion, enCero)) {
            assertThatThrownBy(() -> AgregarPaqueteEntregableCommand.crear(
                    UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(),
                    new DatosArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 10L), Instant.now(), List.of(datos)))
                    .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> assertThat(
                            ex.getValidationResult().tieneErroresDeCampo(
                                    EntregablesFields.ArtefactoEntregable.NUMERO_VERSION)).isTrue());
        }
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoUuidTamanoOTextoInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarPaqueteEntregableCommand.crear(
                "no-uuid", null, new DatosArchivoPaquete(" ", "", null, 0L), null, null))
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
