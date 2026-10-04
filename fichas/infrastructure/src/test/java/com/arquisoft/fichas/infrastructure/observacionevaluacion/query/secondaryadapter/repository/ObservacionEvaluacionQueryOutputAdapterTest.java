package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
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
class ObservacionEvaluacionQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ObservacionEvaluacionEstudianteQueryRepository repository;

    private ObservacionEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionEvaluacionQueryOutputAdapter(repository);
    }

    @Test
    void debeDevolverObservacionesOrdenadas_cuandoElEstudianteEstaVinculadoALaFicha() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var evaluacion = persistirEvaluacion(ficha);
        var revisarMetodologia = persistirObservacion(evaluacion.getId(), "Revisar la metodología");
        var marcoTeorico = persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        var alcance = persistirObservacion(evaluacion.getId(), "Falta delimitar el alcance");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudiante));

        // Assert
        assertThat(resultado)
                .extracting(ObservacionEvaluacionReadModel::id)
                .containsExactly(marcoTeorico, alcance, revisarMetodologia);
        assertThat(resultado.getFirst()).isEqualTo(new ObservacionEvaluacionReadModel(
                marcoTeorico, evaluacion.getId(), "El marco teórico es insuficiente"));
    }

    @Test
    void debeDevolverListaVacia_cuandoElEstudianteNoEstaVinculadoALaFicha() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var vinculado = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, vinculado);
        persistirVinculo(UtilUUID.generarNuevoUUID(), ajeno);
        var evaluacion = persistirEvaluacion(ficha);
        persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), ajeno));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverListaVacia_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var evaluacion = persistirEvaluacion(ficha);
        persistirObservacion(evaluacion.getId(), "El marco teórico es insuficiente");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(
                criteria(UtilUUID.generarNuevoUUID(), estudiante));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIgnorarObservacionesDeOtraEvaluacion_cuandoLaFichaTieneVarias() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var pedida = persistirEvaluacion(ficha);
        var otra = persistirEvaluacion(ficha);
        var buscada = persistirObservacion(pedida.getId(), "Falta delimitar el alcance");
        persistirObservacion(otra.getId(), "Observación de otra evaluación");
        sincronizar();

        // Act
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(pedida.getId(), estudiante));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(buscada);
    }

    @Test
    void debeDevolverCadaObservacionUnaVez_cuandoLaFichaTieneVariosEstudiantes() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudianteUno = UtilUUID.generarNuevoUUID();
        var estudianteDos = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudianteUno);
        persistirVinculo(ficha, estudianteDos);
        var evaluacion = persistirEvaluacion(ficha);
        var compartida = persistirObservacion(evaluacion.getId(), "Observación compartida");
        sincronizar();

        // Act
        var paraUno = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudianteUno));
        var paraDos = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudianteDos));

        // Assert
        assertThat(paraUno).extracting(ObservacionEvaluacionReadModel::id).containsExactly(compartida);
        assertThat(paraDos).extracting(ObservacionEvaluacionReadModel::id).containsExactly(compartida);
    }

    @Test
    void debeIncluirObservaciones_cuandoLaEvaluacionEstaDescartada() {
        // Arrange
        var ficha = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        persistirVinculo(ficha, estudiante);
        var evaluacion = persistirEvaluacion(ficha);
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
        var resultado = adapter.consultarPorEvaluacionYEstudiante(criteria(evaluacion.getId(), estudiante));

        // Assert
        assertThat(resultado).extracting(ObservacionEvaluacionReadModel::id).containsExactly(observacion);
    }

    private static ObservacionEvaluacionEstudianteCriteria criteria(UUID evaluacionFichaPerfil, UUID estudiante) {
        return new ObservacionEvaluacionEstudianteCriteria(evaluacionFichaPerfil, estudiante);
    }

    private void persistirVinculo(UUID fichaPerfilId, UUID estudianteId) {
        entityManager.persist(EstudianteFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build());
    }

    private EvaluacionFichaPerfilJpaEntity persistirEvaluacion(UUID fichaPerfilId) {
        return entityManager.persist(EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .representanteComiteId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(fichaPerfilId)
                .fechaCreacion(Instant.now())
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
