package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteProyectoGradoDomainTest {

    @Test
    void debeCrearUnVinculoPorEstudiante_cuandoLaListaEsValida() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Act
        var vinculos = EstudianteProyectoGradoDomain.crear(proyectoGrado, estudiantes);

        // Assert
        assertThat(vinculos).hasSize(3);
        assertThat(vinculos).extracting(EstudianteProyectoGradoDomain::getEstudiante)
                .containsExactlyElementsOf(estudiantes);
        assertThat(vinculos).extracting(EstudianteProyectoGradoDomain::getProyectoGrado)
                .containsOnly(proyectoGrado);
        assertThat(vinculos).extracting(EstudianteProyectoGradoDomain::getId)
                .doesNotContainNull()
                .doesNotHaveDuplicates();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaListaDeEstudiantesEstaVacia() {
        // Act & Assert
        assertThatThrownBy(() -> EstudianteProyectoGradoDomain.crear(UUID.randomUUID(), List.of()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES);
                            assertThat(error.codigoError())
                                    .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS);
                        }));
    }

    @Test
    void debeAcumularAmbosErrores_cuandoProyectoGradoYEstudianteSonNulos() {
        // Arrange
        var estudiantes = Arrays.asList((UUID) null);

        // Act & Assert
        assertThatThrownBy(() -> EstudianteProyectoGradoDomain.crear(null, estudiantes))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                            ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO);
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var proyectoGrado = UUID.randomUUID();

        // Act
        var vinculo = EstudianteProyectoGradoDomain.reconstruir(id, estudiante, proyectoGrado);

        // Assert
        assertThat(vinculo.getId()).isEqualTo(id);
        assertThat(vinculo.getEstudiante()).isEqualTo(estudiante);
        assertThat(vinculo.getProyectoGrado()).isEqualTo(proyectoGrado);
    }
}
