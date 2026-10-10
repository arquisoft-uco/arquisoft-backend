package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.impl.AsignarEstudiantesProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteDuplicadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteYaVinculadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudiantesNoVigentesException;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesProyectoGradoValidatorTest {

    private final AsignarEstudiantesProyectoGradoValidatorImpl validator =
            new AsignarEstudiantesProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Titulo", UtilUUID.generarNuevoUUID());

    private final ProyectoGradoDomain proyectoFinalizado = ProyectoGradoDomain.reconstruir(
            proyecto.getId(), proyecto.getFichaPerfil(), proyecto.getTituloProyecto(),
            proyecto.getCoordinador(), EstadoProyectoGrado.FINALIZADO);

    private static EstudianteDomain estudiante(UUID id) {
        return EstudianteDomain.reconstruir(id, "2020", "Ana Gomez", "ana.gomez@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private AgregacionEstudiantesProyectoGradoDomain entrada(List<UUID> estudiantes) {
        return AgregacionEstudiantesProyectoGradoDomain.crear(
                EstudianteProyectoGradoDomain.crear(proyecto.getId(), estudiantes));
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(List.of(a, b)), proyecto,
                List.of(estudiante(a), estudiante(b)), List.of(), 1))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDuplicado_antesQueLasDemasReglas_cuandoUnEstudianteSeRepiteYElProyectoNoExiste() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(repetido, repetido)), ProyectoGradoDomain.VACIO,
                List.of(), List.of(), 0))
                .isInstanceOf(EstudianteDuplicadoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(UtilUUID.generarNuevoUUID())),
                ProyectoGradoDomain.VACIO, List.of(), List.of(), 0))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoVigentes_cuandoAlgunEstudianteNoEstaVigenteEnLaReplica() {
        // Arrange
        var vigente = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(vigente, UtilUUID.generarNuevoUUID())), proyecto,
                List.of(estudiante(vigente)), List.of(), 0))
                .isInstanceOf(EstudiantesNoVigentesException.class);
    }

    @Test
    void debeLanzarNoVigentes_antesQueYaVinculados_cuandoAmbosSeCumplen() {
        // Arrange
        var vigente = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(vigente, UtilUUID.generarNuevoUUID())), proyecto,
                List.of(estudiante(vigente)), List.of(vigente), 0))
                .isInstanceOf(EstudiantesNoVigentesException.class);
    }

    @Test
    void debeLanzarYaVinculado_cuandoHayVinculados() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(a)), proyecto,
                List.of(estudiante(a)), List.of(a), 1))
                .isInstanceOf(EstudianteYaVinculadoException.class);
    }

    @Test
    void debeLanzarYaVinculado_antesQueFinalizado_cuandoAmbosSeCumplen() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(a)), proyectoFinalizado,
                List.of(estudiante(a)), List.of(a), 0))
                .isInstanceOf(EstudianteYaVinculadoException.class);
    }

    @Test
    void debeLanzarFinalizado_cuandoElProyectoEstaFinalizado() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(a)), proyectoFinalizado,
                List.of(estudiante(a)), List.of(), 0))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarFinalizado_antesQueCupo_cuandoAmbosSeCumplen() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(a, b)), proyectoFinalizado,
                List.of(estudiante(a), estudiante(b)), List.of(), 2))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarCupoExcedido_cuandoLosVinculadosMasLosNuevosSuperanElMaximo() {
        // Arrange
        var a = UtilUUID.generarNuevoUUID();
        var b = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(a, b)), proyecto,
                List.of(estudiante(a), estudiante(b)), List.of(), 2))
                .isInstanceOf(CupoEstudiantesExcedidoException.class);
    }
}
