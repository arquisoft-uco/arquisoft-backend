package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionCuantitativaAsesorAccesoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaAsesorAccesoQueryRepository repository;

    private EvaluacionCuantitativaAsesorAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCuantitativaAsesorAccesoQueryOutputAdapter(repository);
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
}
