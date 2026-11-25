package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
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
class EstadoFichaPerfilRepresentanteEvaluacionDescartadaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstadoFichaPerfilEstudianteQueryRepository repository;

    @Autowired
    private EstadoFichaPerfilAsesorQueryRepository asesorRepository;

    @Autowired
    private EstadoFichaPerfilRepresentanteQueryRepository representanteRepository;

    @Autowired
    private EstadoFichaPerfilCoordinadorQueryRepository coordinadorRepository;

    private EstadoFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoFichaPerfilQueryOutputAdapter(
                repository,
                asesorRepository,
                new EstadoFichaPerfilAsesorJpaSpecification(),
                representanteRepository,
                coordinadorRepository);

        entityManager.persist(EstadoFichaJpaEntity.builder()
                .id("EN_CONSTRUCCION").nombre("En Construccion").descripcion("").build());
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
        persistirTransicionFicha(ficha);
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
        persistirTransicionFicha(ficha);
        persistirEstadoEvaluacion(evaluacion, "EN_EVALUACION", Instant.now().minusSeconds(120));
        persistirEstadoEvaluacion(evaluacion, "DESCARTADA", Instant.now().minusSeconds(60));
        persistirEstadoEvaluacion(evaluacion, "EN_EVALUACION", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).extracting(EstadoFichaPerfilReadModel::id).containsExactly("EN_CONSTRUCCION");
    }

    @Test
    void debeIncluirFicha_cuandoLaEvaluacionDescartadaEsDeOtroRepresentante() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        persistirEvaluacion(ficha, representante);
        var ajena = persistirEvaluacion(ficha, UtilUUID.generarNuevoUUID());
        persistirTransicionFicha(ficha);
        persistirEstadoEvaluacion(ajena, "DESCARTADA", Instant.now());
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).extracting(EstadoFichaPerfilReadModel::id).containsExactly("EN_CONSTRUCCION");
    }

    @Test
    void debeIncluirFicha_cuandoLaEvaluacionNoTieneEstados() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        persistirEvaluacion(ficha, representante);
        persistirTransicionFicha(ficha);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFichaYRepresentante(ficha, representante);

        // Assert
        assertThat(resultado).extracting(EstadoFichaPerfilReadModel::id).containsExactly("EN_CONSTRUCCION");
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

    private void persistirTransicionFicha(UUID ficha) {
        var estadoRef = entityManager.getEntityManager()
                .getReference(EstadoFichaJpaEntity.class, "EN_CONSTRUCCION");
        entityManager.persist(EstadoFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(ficha)
                .estadoFicha(estadoRef)
                .fechaActualizacion(Instant.now())
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
