package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemFichaPerfilRepresentanteEvaluacionDescartadaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemFichaPerfilQueryRepository repository;

    @Autowired
    private ItemFichaPerfilEstudianteQueryRepository estudianteRepository;

    @Autowired
    private ItemFichaPerfilRepresentanteQueryRepository representanteRepository;

    @Autowired
    private ItemFichaPerfilCoordinadorQueryRepository coordinadorRepository;

    private ItemFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemFichaPerfilQueryOutputAdapter(repository, estudianteRepository, representanteRepository,
                coordinadorRepository);

        entityManager.persist(TipoItemJpaEntity.builder()
                .id("OBJETIVO_GENERAL").nombre("Objetivo General").descripcion("").build());
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("EN_EVALUACION").nombre("En Evaluacion").descripcion("").build());
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("DESCARTADA").nombre("Descartada").descripcion("").build());
    }

    @Test
    void debeExcluirFicha_cuandoEvaluacionDelRepresentanteEstaDescartada() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        var evaluacion = persistirEvaluacion(ficha, representante);
        persistirItem(ficha);
        persistirEstadoEvaluacion(evaluacion, "EN_EVALUACION", Instant.now().minusSeconds(60));
        persistirEstadoEvaluacion(evaluacion, "DESCARTADA", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIncluirFicha_cuandoLaEvaluacionFueReactivada() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        var evaluacion = persistirEvaluacion(ficha, representante);
        persistirItem(ficha);
        persistirEstadoEvaluacion(evaluacion, "EN_EVALUACION", Instant.now().minusSeconds(120));
        persistirEstadoEvaluacion(evaluacion, "DESCARTADA", Instant.now().minusSeconds(60));
        persistirEstadoEvaluacion(evaluacion, "EN_EVALUACION", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    void debeIncluirFicha_cuandoLaEvaluacionDescartadaEsDeOtroRepresentante() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        persistirEvaluacion(ficha, representante);
        var ajena = persistirEvaluacion(ficha, UtilUUID.generarNuevoUUID());
        persistirItem(ficha);
        persistirEstadoEvaluacion(ajena, "DESCARTADA", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    void debeIncluirFicha_cuandoLaEvaluacionNoTieneEstados() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        persistirEvaluacion(ficha, representante);
        persistirItem(ficha);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).hasSize(1);
    }

    private UUID persistirEvaluacion(UUID ficha, UUID representante) {
        var evaluacion = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(ficha)
                .representanteComiteId(representante)
                .fechaCreacion(Instant.now())
                .build();
        entityManager.persist(evaluacion);
        return evaluacion.getId();
    }

    private void persistirItem(UUID ficha) {
        var tipoRef = entityManager.getEntityManager().getReference(TipoItemJpaEntity.class, "OBJETIVO_GENERAL");
        entityManager.persist(ItemFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(ficha)
                .tipoItem(tipoRef)
                .contenido("Contenido")
                .build());
    }

    private void persistirEstadoEvaluacion(UUID evaluacion, String estadoEvaluacionId, Instant fecha) {
        var evaluacionRef = entityManager.getEntityManager()
                .getReference(EvaluacionFichaPerfilJpaEntity.class, evaluacion);
        var estadoRef = entityManager.getEntityManager()
                .getReference(EstadoEvaluacionJpaEntity.class, estadoEvaluacionId);
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacionRef)
                .estadoEvaluacion(estadoRef)
                .fechaActualizacion(fecha)
                .build());
    }
}
