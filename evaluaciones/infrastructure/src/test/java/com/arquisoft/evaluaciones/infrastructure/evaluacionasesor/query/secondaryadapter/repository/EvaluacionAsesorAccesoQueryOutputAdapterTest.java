package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla evaluacion_asesor no tiene @Entity en el lado de comando, asi que Hibernate no la genera
// con ddl-auto. Se crea aqui con el DDL de V20261018233000 para ejercitar el @Subselect real.
@DataJpaTest
class EvaluacionAsesorAccesoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsesorAccesoQueryRepository repository;

    private EvaluacionAsesorAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorAccesoQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_asesor (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL, asesor_id UUID NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
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
}
