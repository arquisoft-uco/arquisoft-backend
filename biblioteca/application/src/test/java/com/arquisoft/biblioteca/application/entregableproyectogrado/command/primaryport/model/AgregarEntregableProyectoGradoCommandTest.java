package com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEntregableProyectoGradoCommandTest {

    @Test
    void debeCrearConUuidsConvertidos_cuandoTodosLosCamposSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarEntregableProyectoGradoCommand.crear(
                entregable.toString(), proyecto.toString(), 2, "APROBADO", "a/b.zip", "b.zip");

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.version()).isEqualTo(2);
        assertThat(command.estadoEntregable()).isEqualTo("APROBADO");
        assertThat(command.claveObjeto()).isEqualTo("a/b.zip");
        assertThat(command.nombreArchivo()).isEqualTo("b.zip");
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEntregableProyectoGradoCommand.crear(
                "no-uuid", null, 0, " ", "", " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                BibliotecaFields.EntregableProyectoGrado.ID,
                                BibliotecaFields.EntregableProyectoGrado.PROYECTO_GRADO,
                                BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE,
                                BibliotecaFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                                BibliotecaFields.EntregableProyectoGrado.CLAVE_OBJETO,
                                BibliotecaFields.EntregableProyectoGrado.NOMBRE_ARCHIVO));
    }

    @Test
    void debeRechazarLaVersion_cuandoEsNula() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID().toString();
        var proyecto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarEntregableProyectoGradoCommand.crear(
                entregable, proyecto, null, "APROBADO", "a/b.zip", "b.zip"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> assertThat(
                        ex.getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(BibliotecaFields.EntregableProyectoGrado.VERSION_ENTREGABLE));
    }
}
