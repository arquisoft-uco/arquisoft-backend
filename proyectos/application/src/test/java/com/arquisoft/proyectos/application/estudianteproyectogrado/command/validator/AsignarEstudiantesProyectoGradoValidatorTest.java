package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.impl.AsignarEstudiantesProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteDuplicadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudiantesNoVigentesException;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
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
            UUID.randomUUID(), "Titulo", UUID.randomUUID());

    private static EstudianteDomain estudiante(UUID id) {
        return EstudianteDomain.reconstruir(id, "2020", "Ana Gomez", "ana.gomez@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), null);
    }

    private AgregacionEstudiantesProyectoGradoDomain entrada(List<UUID> estudiantes) {
        return AgregacionEstudiantesProyectoGradoDomain.crear(
                EstudianteProyectoGradoDomain.crear(proyecto.getId(), estudiantes));
    }

    @Test
    void debePasar_cuandoElProyectoExisteYLosEstudiantesSonVigentesYCabenEnElCupo() {
        // Arrange
        var a = UUID.randomUUID();
        var b = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(List.of(a, b)), proyecto,
                List.of(estudiante(a), estudiante(b)), 1))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDuplicado_antesQueLasDemasReglas_cuandoUnEstudianteSeRepiteYElProyectoNoExiste() {
        // Arrange
        var repetido = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(repetido, repetido)), ProyectoGradoDomain.VACIO,
                List.of(), 0))
                .isInstanceOf(EstudianteDuplicadoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(UUID.randomUUID())), ProyectoGradoDomain.VACIO,
                List.of(), 0))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoVigentes_cuandoAlgunEstudianteNoEstaVigenteEnLaReplica() {
        // Arrange
        var vigente = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(vigente, UUID.randomUUID())), proyecto,
                List.of(estudiante(vigente)), 0))
                .isInstanceOf(EstudiantesNoVigentesException.class);
    }

    @Test
    void debeLanzarCupoExcedido_cuandoLosVinculadosMasLosNuevosSuperanElMaximo() {
        // Arrange
        var a = UUID.randomUUID();
        var b = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(List.of(a, b)), proyecto,
                List.of(estudiante(a), estudiante(b)), 2))
                .isInstanceOf(CupoEstudiantesExcedidoException.class);
    }
}
