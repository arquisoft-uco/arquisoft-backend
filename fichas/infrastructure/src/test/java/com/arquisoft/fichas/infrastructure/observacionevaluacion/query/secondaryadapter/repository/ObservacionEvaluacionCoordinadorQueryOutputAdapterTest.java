package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionCoordinadorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
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
class ObservacionEvaluacionCoordinadorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionEvaluacionEstudianteQueryRepository estudianteRepository;

    @Autowired
    private ObservacionEvaluacionAsesorQueryRepository asesorRepository;

    @Autowired
    private ObservacionEvaluacionRepresentanteQueryRepository representanteRepository;

    @Autowired
    private ObservacionEvaluacionCoordinadorQueryRepository coordinadorRepository;

    private ObservacionEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionEvaluacionQueryOutputAdapter(
                estudianteRepository, asesorRepository, representanteRepository, coordinadorRepository);
    }

    @Test
    void debeDevolverObservacionesDeTodasLasEvaluaciones_cuandoLaFichaTieneVarias() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var primera = persistirEvaluacion(UtilUUID.generarNuevoUUID(), ficha, Instant.parse("2026-09-01T10:00:00Z"));
        var segunda = persistirEvaluacion(UtilUUID.generarNuevoUUID(), ficha, Instant.parse("2026-09-02T10:00:00Z"));
        var deSegundaPrecisar = persistirObservacion(segunda.getId(), "Precisar el alcance");
        var deSegundaAjustar = persistirObservacion(segunda.getId(), "Ajustar los objetivos");
        var deLaPrimera = persistirObservacion(primera.getId(), "Revisar la metodología");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorFicha(new ObservacionEvaluacionCoordinadorCriteria(ficha));

        // Assert
        assertThat(resultado)
                .extracting(ObservacionEvaluacionReadModel::id)
                .containsExactly(deLaPrimera, deSegundaAjustar, deSegundaPrecisar);
        assertThat(resultado.getFirst()).isEqualTo(new ObservacionEvaluacionReadModel(
                deLaPrimera, primera.getId(), "Revisar la metodología"));
    }

    @Test
    void debeIncluirObservacionesDeEvaluacionesDescartadas_cuandoLaEvaluacionEstaDescartada() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var evaluacion = persistirEvaluacion(UtilUUID.generarNuevoUUID(), ficha, Instant.now());
        var descartada = entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("DESCARTADA").nombre("Descartada").descripcion("La evaluación fue descartada").build());
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(descartada)
                .fechaActualizacion(Instant.now())
                .build());
        var observacion = persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorFicha(new ObservacionEvaluacionCoordinadorCriteria(ficha));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(observacion);
    }

    @Test
    void debeNoIntercalarEvaluaciones_cuandoComparteFechaCreacion() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-09-01T10:00:00Z");
        var evaluacionUno = persistirEvaluacion(
                UUID.fromString("00000000-0000-0000-0000-00000000000a"), ficha, fecha);
        var evaluacionDos = persistirEvaluacion(
                UUID.fromString("00000000-0000-0000-0000-00000000000b"), ficha, fecha);
        var deDos = persistirObservacion(evaluacionDos.getId(), "A primera de la segunda");
        var deUnoUltima = persistirObservacion(evaluacionUno.getId(), "Z ultima de la primera");
        var deUnoMedia = persistirObservacion(evaluacionUno.getId(), "M media de la primera");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorFicha(new ObservacionEvaluacionCoordinadorCriteria(ficha));

        // Assert
        assertThat(resultado)
                .extracting(ObservacionEvaluacionReadModel::id)
                .containsExactly(deUnoMedia, deUnoUltima, deDos);
    }

    @Test
    void debeNoDevolverObservacionesDeOtraFicha_cuandoFiltraPorFicha() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var evaluacionPropia = persistirEvaluacion(UtilUUID.generarNuevoUUID(), ficha, Instant.now());
        var evaluacionAjena = persistirEvaluacion(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.now());
        var propia = persistirObservacion(evaluacionPropia.getId(), "Observación propia");
        persistirObservacion(evaluacionAjena.getId(), "Observación ajena");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorFicha(new ObservacionEvaluacionCoordinadorCriteria(ficha));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(propia);
    }

    @Test
    void debeDevolverListaVacia_cuandoLaFichaNoExiste() {
        // Arrange
        var evaluacion = persistirEvaluacion(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.now());
        persistirObservacion(evaluacion.getId(), "Observación");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorFicha(
                new ObservacionEvaluacionCoordinadorCriteria(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }

    private EvaluacionFichaPerfilJpaEntity persistirEvaluacion(UUID id, UUID fichaPerfilId, Instant fechaCreacion) {
        return entityManager.persist(EvaluacionFichaPerfilJpaEntity.builder()
                .id(id)
                .representanteComiteId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(fichaPerfilId)
                .fechaCreacion(fechaCreacion)
                .build());
    }

    private UUID persistirObservacion(UUID evaluacionFichaPerfilId, String texto) {
        return entityManager.persist(ObservacionEvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfilId(evaluacionFichaPerfilId)
                .observacion(texto)
                .build()).getId();
    }

    private void sincronizar() {
        entityManager.flush();
        entityManager.clear();
    }
}
