package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand.Estudiante;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstudiantesProyectoGradoCommandTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    private static Estudiante estudiante(String id) {
        return new Estudiante(id, "Ana Perez", "ana@uco.edu.co");
    }

    @Test
    void debeCrearCommandConUuidYContactoConservados_cuandoElPayloadEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var primero = estudiante(UtilUUID.generarNuevoUUID().toString());
        var segundo = new Estudiante(UtilUUID.generarNuevoUUID().toString(), "Luis", "luis@uco.edu.co");

        // Act
        var command = AgregarEstudiantesProyectoGradoCommand.crear(
                proyectoGrado.toString(), List.of(primero, segundo), OCURRIDO_EN);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiantes()).containsExactly(primero, segundo);
        assertThat(command.ocurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoProyectoEstudianteContactoYOcurridoEnSonInvalidos() {
        // Arrange
        var sinContacto = new Estudiante(UtilUUID.generarNuevoUUID().toString(), null, null);
        var estudiantes = List.of(estudiante("no-uuid"), sinContacto);

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear("no-uuid", estudiantes, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                                EntregablesFields.EstudianteProyectoGrado.ESTUDIANTE,
                                EntregablesFields.EstudianteProyectoGrado.NOMBRE,
                                EntregablesFields.EstudianteProyectoGrado.EMAIL,
                                EntregablesFields.EstudianteProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeRechazar_cuandoLaListaEstaVaciaONula() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyectoGrado, List.of(), OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EntregablesFields.EstudianteProyectoGrado.ESTUDIANTES));
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyectoGrado, null, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeRechazar_cuandoHayEstudiantesDuplicados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();
        var id = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(
                proyectoGrado, List.of(estudiante(id), estudiante(id)), OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EntregablesFields.EstudianteProyectoGrado.ESTUDIANTES));
    }

    @Test
    void debeRechazar_cuandoElProyectoEsElUuidDeCeros() {
        // Arrange
        var estudiantes = List.of(estudiante(UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(
                UtilUUID.obtenerPorDefectoComoTexto(), estudiantes, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EntregablesFields.EstudianteProyectoGrado.PROYECTO_GRADO));
    }
}
