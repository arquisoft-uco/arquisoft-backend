package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla evaluacion_asesor no tiene @Entity en el lado de comando, asi que Hibernate no la genera
// con ddl-auto. Se crea aqui con el DDL de V20261018233000 para ejercitar el @Subselect real.
// @DirtiesContext: el DDL nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsesorAccesoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsesorAccesoQueryRepository repository;

    @Autowired
    private EstudianteEvaluacionAsesorAccesoQueryRepository estudianteRepository;

    private EvaluacionAsesorAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorAccesoQueryOutputAdapter(repository, estudianteRepository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_asesor (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL, asesor_id UUID NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                ALTER TABLE entregable ADD COLUMN IF NOT EXISTS proyecto_grado_id UUID
                    DEFAULT '00000000-0000-0000-0000-000000000000' NOT NULL
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacionAsesorDelProyecto(UUID proyectoGrado) {
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable, proyecto_grado_id) VALUES (?, ?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, "Proyecto " + entregable)
                .setParameter(3, 1)
                .setParameter(4, proyectoGrado)
                .executeUpdate();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, "PENDIENTE")
                .executeUpdate();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionAsesor)
                .setParameter(2, evaluacion)
                .setParameter(3, UtilUUID.generarNuevoUUID())
                .executeUpdate();
        return evaluacionAsesor;
    }

    private void sembrarVinculo(UUID estudiante, UUID proyectoGrado, Instant eliminadoEn) {
        entityManager.createNativeQuery("""
                        INSERT INTO estudiante_proyecto_grado
                            (id, estudiante_id, proyecto_grado_id, ocurrido_en, eliminado_en)
                        VALUES (?, ?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, estudiante)
                .setParameter(3, proyectoGrado)
                .setParameter(4, Timestamp.from(Instant.parse("2026-10-20T10:00:00Z")))
                .setParameter(5, UtilObjeto.esNulo(eliminadoEn) ? null : Timestamp.from(eliminadoEn))
                .executeUpdate();
    }

    @Test
    void debeRetornarElAsesor_cuandoLaEvaluacionAsesorExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionAsesor)
                .setParameter(2, UtilUUID.generarNuevoUUID())
                .setParameter(3, asesor)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerAsesorPorId(evaluacionAsesor);

        // Assert
        assertThat(resultado).contains(asesor);
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionAsesorNoExiste() {
        // Act
        var resultado = adapter.obtenerAsesorPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverElIdDeLaEvaluacionAsesor_cuandoLaEvaluacionLaTiene() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionAsesor)
                .setParameter(2, evaluacion)
                .setParameter(3, UtilUUID.generarNuevoUUID())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorEvaluacion(evaluacion);

        // Assert
        assertThat(resultado).contains(evaluacionAsesor);
    }

    @Test
    void debeDevolverVacio_cuandoLaEvaluacionNoLaTiene() {
        // Act
        var resultado = adapter.obtenerPorEvaluacion(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverEstudiantesVigentes_cuandoHayVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudianteUno = UtilUUID.generarNuevoUUID();
        var estudianteDos = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarEvaluacionAsesorDelProyecto(proyecto);
        sembrarVinculo(estudianteUno, proyecto, null);
        sembrarVinculo(estudianteDos, proyecto, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(evaluacionAsesor);

        // Assert
        assertThat(resultado).containsExactlyInAnyOrder(estudianteUno, estudianteDos);
    }

    @Test
    void debeExcluirEstudianteDadoDeBaja_cuandoTieneEliminadoEn() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var dadoDeBaja = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarEvaluacionAsesorDelProyecto(proyecto);
        sembrarVinculo(vigente, proyecto, null);
        sembrarVinculo(dadoDeBaja, proyecto, Instant.parse("2026-10-25T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(evaluacionAsesor);

        // Assert
        assertThat(resultado).containsExactly(vigente);
    }

    @Test
    void debeExcluirEstudiantesDeOtroProyecto_cuandoSeConsulta() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var propio = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarEvaluacionAsesorDelProyecto(proyecto);
        sembrarVinculo(propio, proyecto, null);
        sembrarVinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(evaluacionAsesor);

        // Assert
        assertThat(resultado).containsExactly(propio);
    }

    @Test
    void debeDevolverVacio_cuandoEvaluacionNoExiste() {
        // Act
        var resultado = adapter.obtenerEstudiantesVigentesPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
