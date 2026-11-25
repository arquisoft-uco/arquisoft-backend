package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.exception.EvaluacionAsesorNoDisponibleException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// asesor y evaluacion_asesor se crean aqui con el DDL de V20261018233000 (sin FK) para ejercitar el @Subselect real.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito y rompe el rollback entre clases.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsesorDeEvaluacionQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsesorDeEvaluacionQueryRepository repository;

    private EvaluacionAsesorDeEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorDeEvaluacionQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS asesor (
                    id UUID NOT NULL, identificador VARCHAR(30) NOT NULL, nombre VARCHAR(50) NOT NULL,
                    email VARCHAR(50) NOT NULL, eliminado_en TIMESTAMP WITH TIME ZONE NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_asesor (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL, asesor_id UUID NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarAsesor(String nombre, String email, boolean dadoDeBaja) {
        var asesor = UtilUUID.generarNuevoUUID();
        var eliminadoEn = dadoDeBaja ? "CURRENT_TIMESTAMP" : "NULL";
        entityManager.createNativeQuery(
                        "INSERT INTO asesor (id, identificador, nombre, email, eliminado_en) VALUES (?, ?, ?, ?, "
                                + eliminadoEn + ")")
                .setParameter(1, asesor)
                .setParameter(2, "ID-" + asesor.toString().substring(0, 8))
                .setParameter(3, nombre)
                .setParameter(4, email)
                .executeUpdate();
        return asesor;
    }

    private UUID sembrarEvaluacionAsesor(UUID evaluacion, UUID asesor) {
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionAsesor)
                .setParameter(2, evaluacion)
                .setParameter(3, asesor)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionAsesor;
    }

    private void fijarNotas(UUID fila) {
        entityManager.createNativeQuery("""
                        UPDATE evaluacion_asesor SET resumen_cualitativo = ?, resumen_cuantitativo = ?, nota_acumulada = ?
                        WHERE id = ?
                        """)
                .setParameter(1, new BigDecimal("4.50"))
                .setParameter(2, new BigDecimal("3.80"))
                .setParameter(3, new BigDecimal("4.10"))
                .setParameter(4, fila)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeMapearNotas_desdeLosAliasDelSubselect() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarEvaluacionAsesor(evaluacion, sembrarAsesor("Ana Gomez", "ana@uco.edu.co", false));
        fijarNotas(evaluacionAsesor);

        // Act
        var resultado = adapter.consultar(new EvaluacionAsesorDeEvaluacionCriteria(evaluacion));

        // Assert
        var notas = resultado.notas();
        assertThat(notas.resumenCualitativo()).isEqualByComparingTo("4.50");
        assertThat(notas.resumenCuantitativo()).isEqualByComparingTo("3.80");
        assertThat(notas.notaAcumulada()).isEqualByComparingTo("4.10");
    }

    @Test
    void debeRetornarNotasNulas_cuandoNoSeCalcularon() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        sembrarEvaluacionAsesor(evaluacion, sembrarAsesor("Ana Gomez", "ana@uco.edu.co", false));

        // Act
        var resultado = adapter.consultar(new EvaluacionAsesorDeEvaluacionCriteria(evaluacion));

        // Assert
        assertThat(resultado.notas().resumenCualitativo()).isNull();
        assertThat(resultado.notas().resumenCuantitativo()).isNull();
        assertThat(resultado.notas().notaAcumulada()).isNull();
    }

    @Test
    void debeDevolverEvaluacionYAsesor_cuandoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = sembrarAsesor("Ana Gomez", "ana@uco.edu.co", false);
        var evaluacionAsesor = sembrarEvaluacionAsesor(evaluacion, asesor);

        // Act
        var resultado = adapter.consultar(new EvaluacionAsesorDeEvaluacionCriteria(evaluacion));

        // Assert
        assertThat(resultado.id()).isEqualTo(evaluacionAsesor);
        assertThat(resultado.asesor().id()).isEqualTo(asesor);
        assertThat(resultado.asesor().nombre()).isEqualTo("Ana Gomez");
        assertThat(resultado.asesor().email()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeIncluirAsesorDadoDeBaja_cuandoTieneEliminadoEn() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = sembrarAsesor("Luis Perez", "luis@uco.edu.co", true);
        var evaluacionAsesor = sembrarEvaluacionAsesor(evaluacion, asesor);

        // Act
        var resultado = adapter.consultar(new EvaluacionAsesorDeEvaluacionCriteria(evaluacion));

        // Assert
        assertThat(resultado.id()).isEqualTo(evaluacionAsesor);
        assertThat(resultado.asesor().id()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarNoDisponible_cuandoNoHayFila() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionAsesorDeEvaluacionCriteria(evaluacion);

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(criteria))
                .isInstanceOfSatisfying(EvaluacionAsesorNoDisponibleException.class, exception ->
                        assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.NO_DISPONIBLE));
    }
}
