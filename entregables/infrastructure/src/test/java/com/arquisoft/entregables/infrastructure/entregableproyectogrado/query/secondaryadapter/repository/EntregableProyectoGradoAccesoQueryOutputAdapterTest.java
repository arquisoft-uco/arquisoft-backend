package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
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
    void debeRetornarElAsesorVigente_cuandoElProyectoTieneVinculoVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-A").nombre("Asesor").email("a@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, asesor));
        entityManager.persist(vinculo(asesor, proyecto, "2026-02-01T00:00:00Z", null));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAsesorVigentePorId(entregable);

        // Assert
        assertThat(resultado).contains(asesor);
    }

    @Test
    void debeRetornarElAsesorNuevo_cuandoElProyectoCambioDeAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(anterior).identificador("ID-AN").nombre("Anterior").email("an@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, anterior));
        entityManager.persist(vinculo(anterior, proyecto, "2026-02-01T00:00:00Z", "2026-03-01T00:00:00Z"));
        entityManager.persist(vinculo(nuevo, proyecto, "2026-03-01T00:00:01Z", null));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAsesorVigentePorId(entregable);

        // Assert
        assertThat(resultado).contains(nuevo);
    }

    @Test
    void debeRetornarVacio_cuandoElVinculoMasRecienteEstaFinalizado() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-FI").nombre("Asesor").email("fi@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, asesor));
        entityManager.persist(vinculo(asesor, proyecto, "2026-02-01T00:00:00Z", "2026-03-01T00:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAsesorVigentePorId(entregable);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExiste() {
        // Act
        var resultado = adapter.obtenerAsesorVigentePorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarElAcceso_cuandoElEntregableExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-C").nombre("Asesor").email("c@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 3, asesor));
        entityManager.persist(vinculo(asesor, proyecto, "2026-02-01T00:00:00Z", null));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAccesoPorId(entregable);

        // Assert
        assertThat(resultado).hasValueSatisfying(acceso -> {
            assertThat(acceso.id()).isEqualTo(entregable);
            assertThat(acceso.proyectoGrado()).isEqualTo(proyecto);
            assertThat(acceso.version()).isEqualTo(3);
            assertThat(acceso.asesorVigente()).isEqualTo(asesor);
            assertThat(acceso.proyectoCancelado()).isFalse();
        });
    }

    @Test
    void debeMarcarProyectoCancelado_cuandoElProyectoEstaCancelado() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("CANCELADO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-D").nombre("Asesor").email("d@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 2, asesor));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAccesoPorId(entregable);

        // Assert
        assertThat(resultado).hasValueSatisfying(acceso -> assertThat(acceso.proyectoCancelado()).isTrue());
    }

    @Test
    void debeNoMarcarProyectoCancelado_cuandoElProyectoEstaFinalizado() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("FINALIZADO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-F").nombre("Asesor").email("f@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 2, asesor));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAccesoPorId(entregable);

        // Assert
        assertThat(resultado).hasValueSatisfying(acceso -> assertThat(acceso.proyectoCancelado()).isFalse());
    }

    @Test
    void debeRetornarVacioElAcceso_cuandoElEntregableNoExiste() {
        // Act
        var resultado = adapter.obtenerAccesoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    private static AsesorProyectoGradoJpaEntity vinculo(UUID asesor, UUID proyecto, String ocurridoEn, String finalizadoEn) {
        return AsesorProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).asesorId(asesor).proyectoGradoId(proyecto)
                .ocurridoEn(Instant.parse(ocurridoEn))
                .finalizadoEn(finalizadoEn == null ? null : Instant.parse(finalizadoEn))
                .build();
    }

    private UUID sembrarEntregable(UUID proyecto) {
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Proyecto").estadoProyectoId("EN_PROCESO").build());
        entityManager.persist(AsesorJpaEntity.builder()
                .id(asesor).identificador("ID-B").nombre("Asesor").email("b@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, asesor));
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
