package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla entregable no tiene @Entity en el lado de comando: se crea con el DDL nativo para ejercitar el
// @Subselect real. @DirtiesContext: el DDL nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionCuantitativaAsesorAccesoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaAsesorAccesoQueryRepository repository;

    @Autowired
    private EstudianteEvaluacionCuantitativaAsesorAccesoQueryRepository estudianteRepository;

    private EvaluacionCuantitativaAsesorAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCuantitativaAsesorAccesoQueryOutputAdapter(repository, estudianteRepository);
        entityManager.getEntityManager().createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.getEntityManager().createNativeQuery("""
                ALTER TABLE entregable ADD COLUMN IF NOT EXISTS proyecto_grado_id UUID
                    DEFAULT '00000000-0000-0000-0000-000000000000' NOT NULL
                """).executeUpdate();
    }

    private UUID sembrarEvaluacionCuantitativaDelProyecto(UUID proyectoGrado) {
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var evaluacionCuantitativa = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager().createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable, proyecto_grado_id) VALUES (?, ?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, "Proyecto " + entregable)
                .setParameter(3, 1)
                .setParameter(4, proyectoGrado)
                .executeUpdate();
        entityManager.persist(EvaluacionJpaEntity.builder()
                .id(evaluacion).entregable(entregable).estadoEvaluacion("PENDIENTE").build());
        entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(evaluacionAsesor).evaluacion(evaluacion).asesor(UtilUUID.generarNuevoUUID()).build());
        entityManager.persist(EvaluacionCuantitativaAsesorJpaEntity.builder()
                .id(evaluacionCuantitativa)
                .evaluacionAsesor(evaluacionAsesor)
                .item(UtilUUID.generarNuevoUUID())
                .puntaje(3)
                .build());
        return evaluacionCuantitativa;
    }

    private void sembrarVinculo(UUID estudiante, UUID proyectoGrado, Instant eliminadoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(estudiante)
                .proyectoGradoId(proyectoGrado)
                .ocurridoEn(Instant.parse("2026-10-20T10:00:00Z"))
                .eliminadoEn(eliminadoEn)
                .build());
    }

    @Test
    void debeObtenerAcceso_conLaEvaluacionAsesorYElAsesorAsignadoResueltosPorElJoin() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluacionCuantitativa = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(evaluacionAsesor)
                .evaluacion(UtilUUID.generarNuevoUUID())
                .asesor(asesor)
                .build());
        entityManager.persist(EvaluacionCuantitativaAsesorJpaEntity.builder()
                .id(evaluacionCuantitativa)
                .evaluacionAsesor(evaluacionAsesor)
                .item(UtilUUID.generarNuevoUUID())
                .puntaje(3)
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var acceso = adapter.obtenerAccesoPorId(evaluacionCuantitativa);

        // Assert
        assertThat(acceso).isPresent();
        assertThat(acceso.get().id()).isEqualTo(evaluacionCuantitativa);
        assertThat(acceso.get().evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(acceso.get().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionCuantitativaNoExiste() {
        // Act
        var acceso = adapter.obtenerAccesoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(acceso).isEmpty();
    }

    @Test
    void debeDevolverEstudiantesVigentes_delProyectoDeLaEvaluacion() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudianteUno = UtilUUID.generarNuevoUUID();
        var estudianteDos = UtilUUID.generarNuevoUUID();
        var evaluacionCuantitativa = sembrarEvaluacionCuantitativaDelProyecto(proyecto);
        sembrarVinculo(estudianteUno, proyecto, null);
        sembrarVinculo(estudianteDos, proyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(evaluacionCuantitativa);

        // Assert
        assertThat(resultado).containsExactlyInAnyOrder(estudianteUno, estudianteDos);
    }

    @Test
    void debeExcluirVinculoEliminadoYEstudiantesDeOtroProyecto_cuandoSeConsulta() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var evaluacionCuantitativa = sembrarEvaluacionCuantitativaDelProyecto(proyecto);
        sembrarVinculo(vigente, proyecto, null);
        sembrarVinculo(UtilUUID.generarNuevoUUID(), proyecto, Instant.parse("2026-10-25T10:00:00Z"));
        sembrarVinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(evaluacionCuantitativa);

        // Assert
        assertThat(resultado).containsExactly(vigente);
    }

    @Test
    void debeDevolverVacio_cuandoLaEvaluacionNoExisteAlBuscarEstudiantes() {
        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
