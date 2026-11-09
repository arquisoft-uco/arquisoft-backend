package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EntregableProyectoGradoAccesoQueryOutputAdapter.class)
class EntregableProyectoGradoAccesoQueryOutputAdapterTest {

    @Autowired
    private EntregableProyectoGradoAccesoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarElAsesor_cuandoElEntregableExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-A").nombre("Asesor").email("a@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, asesor, "APROBADO"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAsesorPorId(entregable);

        // Assert
        assertThat(resultado).contains(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExiste() {
        // Act
        var resultado = adapter.obtenerAsesorPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private UUID sembrarEntregable(UUID proyecto) {
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-B").nombre("Asesor").email("b@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, asesor, "APROBADO"));
        return entregable;
    }

    private void sembrarEstudiante(UUID estudiante, UUID proyecto, Instant eliminadoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).estudianteId(estudiante).proyectoGradoId(proyecto)
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).eliminadoEn(eliminadoEn).build());
    }

    @Test
    void debeAfirmarParticipacion_cuandoEstudianteVigenteDelProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var entregable = sembrarEntregable(proyecto);
        sembrarEstudiante(estudiante, proyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.estudianteParticipa(entregable, estudiante)).isTrue();
    }

    @Test
    void debeNegar_cuandoEstudianteDadoDeBaja() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var entregable = sembrarEntregable(proyecto);
        sembrarEstudiante(estudiante, proyecto, Instant.parse("2026-02-01T00:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.estudianteParticipa(entregable, estudiante)).isFalse();
    }

    @Test
    void debeNegar_cuandoEstudianteDeOtroProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var entregable = sembrarEntregable(proyecto);
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(otroProyecto).nombre("Otro").estadoProyectoId("EN_PROCESO").build());
        sembrarEstudiante(estudiante, otroProyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.estudianteParticipa(entregable, estudiante)).isFalse();
    }
}
