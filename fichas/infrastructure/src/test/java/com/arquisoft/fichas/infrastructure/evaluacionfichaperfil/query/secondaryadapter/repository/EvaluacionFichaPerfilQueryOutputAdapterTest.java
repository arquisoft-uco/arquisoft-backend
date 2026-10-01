package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EvaluacionFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionFichaPerfilQueryRepository repository;

    private EvaluacionFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionFichaPerfilQueryOutputAdapter(repository);
        persistirEstado("EN_EVALUACION", "En Evaluación");
        persistirEstado("APROBADA", "Aprobada");
    }

    @Test
    void debeDevolverEvaluacionConUltimoEstado_cuandoHayVariasFilasDeTrazabilidad() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var evaluacionId = persistirEvaluacion(ficha, representante, Instant.now());
        var base = Instant.now().minus(2, ChronoUnit.HOURS);
        persistirTrazabilidad(evaluacionId, "EN_EVALUACION", base);
        persistirTrazabilidad(evaluacionId, "APROBADA", base.plus(1, ChronoUnit.HOURS));
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacionId);
            assertThat(e.fichaPerfilId()).isEqualTo(ficha);
            assertThat(e.fechaCreacion()).isNotNull();
            assertThat(e.estadoEvaluacion()).isEqualTo("APROBADA");
            assertThat(e.estadoEvaluacionNombre()).isEqualTo("Aprobada");
        });
    }

    @Test
    void debeDevolverEstadoNulo_cuandoLaEvaluacionNoTieneTrazabilidad() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var evaluacionId = persistirEvaluacion(ficha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).singleElement().satisfies(e -> {
            assertThat(e.id()).isEqualTo(evaluacionId);
            assertThat(e.estadoEvaluacion()).isNull();
            assertThat(e.estadoEvaluacionNombre()).isNull();
        });
    }

    @Test
    void debeFiltrarPorRepresentante_ignorandoEvaluacionesDeOtro() {
        // Arrange
        var ficha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var otroRepresentante = UUID.randomUUID();
        persistirEvaluacion(ficha, representante, Instant.now());
        persistirEvaluacion(ficha, otroRepresentante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).singleElement()
                .satisfies(e -> assertThat(e.fichaPerfilId()).isEqualTo(ficha));
    }

    @Test
    void debeFiltrarPorFicha_ignorandoOtrasFichasDelMismoRepresentante() {
        // Arrange
        var fichaPedida = UUID.randomUUID();
        var otraFicha = UUID.randomUUID();
        var representante = UUID.randomUUID();
        var evaluacionPedida = persistirEvaluacion(fichaPedida, representante, Instant.now());
        persistirEvaluacion(otraFicha, representante, Instant.now());
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionFichaPerfilReadModel> resultado =
                adapter.consultarPorFichaYRepresentante(fichaPedida, representante);

        // Assert
        assertThat(resultado).singleElement()
                .satisfies(e -> assertThat(e.id()).isEqualTo(evaluacionPedida));
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayEvaluacionesPropiasEnEsaFicha() {
        // Act & Assert
        assertThat(adapter.consultarPorFichaYRepresentante(UUID.randomUUID(), UUID.randomUUID()))
                .isEmpty();
    }

    private void persistirEstado(String id, String nombre) {
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(nombre)
                .descripcion("Descripción de " + nombre)
                .build());
    }

    private UUID persistirEvaluacion(UUID fichaId, UUID representanteId, Instant fechaCreacion) {
        var evaluacion = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .representanteComiteId(representanteId)
                .fechaCreacion(fechaCreacion)
                .build();
        entityManager.persist(evaluacion);
        return evaluacion.getId();
    }

    private void persistirTrazabilidad(UUID evaluacionId, String estadoId, Instant fechaActualizacion) {
        var evaluacionRef = entityManager.getEntityManager()
                .getReference(EvaluacionFichaPerfilJpaEntity.class, evaluacionId);
        var estadoRef = entityManager.getEntityManager()
                .getReference(EstadoEvaluacionJpaEntity.class, estadoId);
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionFichaPerfil(evaluacionRef)
                .estadoEvaluacion(estadoRef)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }
}
