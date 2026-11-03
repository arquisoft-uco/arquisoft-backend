package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.exception.EvaluacionAsesorRealizadaNoDisponibleException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// @DirtiesContext: el CREATE TABLE nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsesorRealizadaQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsesorRealizadaQueryRepository repository;

    private EvaluacionAsesorRealizadaQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorRealizadaQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS asesor (
                    id UUID NOT NULL, identificador VARCHAR(30) NOT NULL, nombre VARCHAR(50) NOT NULL,
                    email VARCHAR(50) NOT NULL, eliminado_en TIMESTAMP WITH TIME ZONE NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL, nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_asesor (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL, asesor_id UUID NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery(
                        "MERGE INTO estado_evaluacion (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, "EN_PROGRESO")
                .setParameter(2, "En progreso")
                .setParameter(3, "En progreso")
                .executeUpdate();
        entityManager.flush();
    }

    private record Sembrado(UUID evaluacionAsesor, UUID evaluacion, UUID entregable, UUID asesor) {}

    private Sembrado sembrar(boolean asesorDadoDeBaja) {
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var eliminadoEn = asesorDadoDeBaja ? "CURRENT_TIMESTAMP" : "NULL";
        entityManager.createNativeQuery(
                        "INSERT INTO asesor (id, identificador, nombre, email, eliminado_en) VALUES (?, ?, ?, ?, "
                                + eliminadoEn + ")")
                .setParameter(1, asesor)
                .setParameter(2, "ID-" + asesor.toString().substring(0, 8))
                .setParameter(3, "Ana Gomez")
                .setParameter(4, "ana@uco.edu.co")
                .executeUpdate();
        entityManager.createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable) VALUES (?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, "Robot seguidor")
                .setParameter(3, 2)
                .executeUpdate();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, "EN_PROGRESO")
                .executeUpdate();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionAsesor)
                .setParameter(2, evaluacion)
                .setParameter(3, asesor)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return new Sembrado(evaluacionAsesor, evaluacion, entregable, asesor);
    }

    private static EvaluacionAsesorRealizadaCriteria criteria(UUID evaluacionAsesor) {
        return new EvaluacionAsesorRealizadaCriteria(evaluacionAsesor, UtilUUID.generarNuevoUUID());
    }

    @Test
    void debeDevolverEvaluacionEntregableEstadoYAsesor_cuandoExiste() {
        // Arrange
        var sembrado = sembrar(false);

        // Act
        var resultado = adapter.consultar(criteria(sembrado.evaluacionAsesor()));

        // Assert
        assertThat(resultado).isEqualTo(new EvaluacionAsesorRealizadaReadModel(
                sembrado.evaluacionAsesor(),
                new EvaluacionAsesorRealizadaReadModel.Evaluacion(sembrado.evaluacion(),
                        new EvaluacionAsesorRealizadaReadModel.Entregable(sembrado.entregable(), "Robot seguidor", 2),
                        new EvaluacionAsesorRealizadaReadModel.Estado("EN_PROGRESO", "En progreso")),
                new EvaluacionAsesorRealizadaReadModel.Asesor(sembrado.asesor(), "Ana Gomez", "ana@uco.edu.co")));
    }

    @Test
    void debeIncluirAsesorDadoDeBaja_cuandoTieneEliminadoEn() {
        // Arrange
        var sembrado = sembrar(true);

        // Act
        var resultado = adapter.consultar(criteria(sembrado.evaluacionAsesor()));

        // Assert
        assertThat(resultado.asesor().id()).isEqualTo(sembrado.asesor());
    }

    @Test
    void debeLanzarNoDisponible_cuandoNoHayFila() {
        // Arrange
        var inexistente = criteria(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(inexistente))
                .isInstanceOfSatisfying(EvaluacionAsesorRealizadaNoDisponibleException.class, exception ->
                        assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.REALIZADA_NO_DISPONIBLE));
    }
}
