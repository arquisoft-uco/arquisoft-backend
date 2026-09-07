package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoEstudianteAccesoIdTest {

    @Test
    void debeSerIguales_cuandoProyectoYEstudianteCoinciden() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var id1 = new ProyectoEstudianteAccesoId(proyecto, estudiante);
        var id2 = new ProyectoEstudianteAccesoId(proyecto, estudiante);

        // Act & Assert
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void noDebeSerIguales_cuandoElProyectoOElEstudianteDifieren() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        var base = new ProyectoEstudianteAccesoId(proyecto, estudiante);
        var proyectoDistinto = new ProyectoEstudianteAccesoId(UUID.randomUUID(), estudiante);
        var estudianteDistinto = new ProyectoEstudianteAccesoId(proyecto, UUID.randomUUID());

        // Act & Assert
        assertThat(base).isNotEqualTo(proyectoDistinto);
        assertThat(base).isNotEqualTo(estudianteDistinto);
    }

    @Test
    void noDebeSerIgual_aNuloOAUnTipoDistinto() {
        // Arrange
        var id = new ProyectoEstudianteAccesoId(UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert
        assertThat(id).isNotEqualTo(null);
        assertThat(id).isNotEqualTo("no-es-un-id");
        assertThat(id).isEqualTo(id);
    }

    @Test
    void debeConstruirVacio_cuandoSeUsaElConstructorSinArgumentos() {
        // Act
        var id = new ProyectoEstudianteAccesoId();

        // Assert
        assertThat(id).isEqualTo(new ProyectoEstudianteAccesoId());
    }
}
