package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
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
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ObservacionEvaluacionCommandRepositoryTest {

    private static final String OBSERVACION = "El marco teórico es insuficiente";
    private static final String OTRA_OBSERVACION = "Falta delimitar el alcance";

    @Autowired
    private ObservacionEvaluacionCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID evaluacionFichaPerfilId;

    @BeforeEach
    void sembrar() {
        evaluacionFichaPerfilId = UtilUUID.generarNuevoUUID();
        entityManager.persist(ObservacionEvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfilId(evaluacionFichaPerfilId)
                .observacion(OBSERVACION)
                .build());
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarTrue_cuandoElParEvaluacionTextoYaExiste() {
        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfilId, OBSERVACION);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElTextoEsDistintoEnLaMismaEvaluacion() {
        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfilId, "Otro texto");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarFalse_cuandoElTextoCoincideEnOtraEvaluacion() {
        // Act
        var existe = repository.existsByEvaluacionFichaPerfilIdAndObservacion(
                UtilUUID.generarNuevoUUID(), OBSERVACION);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarPertenenciaConElUltimoEstado_cuandoElRepresentanteEsPropietario() {
        // Arrange
        var evaluacion = persistirEvaluacion();
        var ahora = Instant.now();
        persistirEstadoEvaluacionFicha(evaluacion, persistirEstado("EN_EVALUACION"), ahora.minusSeconds(60));
        persistirEstadoEvaluacionFicha(evaluacion, persistirEstado("APROBADA"), ahora);
        var observacion = persistirObservacion(evaluacion.getId(), OBSERVACION);
        sincronizar();

        // Act
        var pertenencia = repository.obtenerPertenencia(observacion.getId(), evaluacion.getRepresentanteComiteId());

        // Assert
        assertThat(pertenencia).contains(
                new PertenenciaObservacionEvaluacionEntity(evaluacion.getId(), true, "APROBADA"));
    }

    @Test
    void debeRetornarNoPropietario_cuandoElRepresentanteEsOtro() {
        // Arrange
        var evaluacion = persistirEvaluacion();
        persistirEstadoEvaluacionFicha(evaluacion, persistirEstado("EN_EVALUACION"), Instant.now());
        var observacion = persistirObservacion(evaluacion.getId(), OBSERVACION);
        sincronizar();

        // Act
        var pertenencia = repository.obtenerPertenencia(observacion.getId(), UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(pertenencia).contains(
                new PertenenciaObservacionEvaluacionEntity(evaluacion.getId(), false, "EN_EVALUACION"));
    }

    @Test
    void debeRetornarEstadoNulo_cuandoLaEvaluacionNoTieneEstados() {
        // Arrange — el LEFT JOIN no debe degradarse a inner join y perder la fila
        var evaluacion = persistirEvaluacion();
        var observacion = persistirObservacion(evaluacion.getId(), OBSERVACION);
        sincronizar();

        // Act
        var pertenencia = repository.obtenerPertenencia(observacion.getId(), evaluacion.getRepresentanteComiteId());

        // Assert
        assertThat(pertenencia).contains(
                new PertenenciaObservacionEvaluacionEntity(evaluacion.getId(), true, null));
    }

    @Test
    void debeRetornarVacio_cuandoLaObservacionNoExiste() {
        // Act
        var pertenencia = repository.obtenerPertenencia(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(pertenencia).isEmpty();
    }

    @Test
    void debeRetornarFalse_cuandoElTextoEsElDeLaPropiaObservacion() {
        // Arrange
        var observacion = persistirObservacion(evaluacionFichaPerfilId, OTRA_OBSERVACION);
        sincronizar();

        // Act
        var existe = repository.existeOtraConMismoTexto(observacion.getId(), OTRA_OBSERVACION);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoOtraObservacionDeLaMismaEvaluacionTieneElTexto() {
        // Arrange
        var observacion = persistirObservacion(evaluacionFichaPerfilId, OTRA_OBSERVACION);
        sincronizar();

        // Act
        var existe = repository.existeOtraConMismoTexto(observacion.getId(), OBSERVACION);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElTextoSoloExisteEnOtraEvaluacion() {
        // Arrange
        var observacion = persistirObservacion(UtilUUID.generarNuevoUUID(), OTRA_OBSERVACION);
        sincronizar();

        // Act
        var existe = repository.existeOtraConMismoTexto(observacion.getId(), OBSERVACION);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debePersistirElNuevoTexto_cuandoActualizaLaObservacion() {
        // Arrange
        var observacion = persistirObservacion(evaluacionFichaPerfilId, OTRA_OBSERVACION);
        sincronizar();

        // Act
        var filas = repository.actualizarObservacion(observacion.getId(), "Texto corregido");

        // Assert
        assertThat(filas).isEqualTo(1);
        var actualizada = entityManager.find(ObservacionEvaluacionJpaEntity.class, observacion.getId());
        assertThat(actualizada.getObservacion()).isEqualTo("Texto corregido");
        assertThat(actualizada.getEvaluacionFichaPerfilId()).isEqualTo(evaluacionFichaPerfilId);
    }

    @Test
    void debeEliminarSoloLaFilaYLiberarElTexto_cuandoRemueveUnaObservacion() {
        // Arrange
        var removida = persistirObservacion(evaluacionFichaPerfilId, OTRA_OBSERVACION);
        sincronizar();

        // Act
        repository.deleteById(removida.getId());
        sincronizar();

        // Assert — la otra observación de la evaluación sigue y el UNIQUE ya no bloquea el mismo texto
        assertThat(entityManager.find(ObservacionEvaluacionJpaEntity.class, removida.getId())).isNull();
        assertThat(repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfilId, OBSERVACION))
                .isTrue();
        assertThat(repository.existsByEvaluacionFichaPerfilIdAndObservacion(evaluacionFichaPerfilId, OTRA_OBSERVACION))
                .isFalse();
        var reagregada = persistirObservacion(evaluacionFichaPerfilId, OTRA_OBSERVACION);
        sincronizar();
        assertThat(entityManager.find(ObservacionEvaluacionJpaEntity.class, reagregada.getId())).isNotNull();
    }

    private ObservacionEvaluacionJpaEntity persistirObservacion(UUID evaluacionFichaPerfil, String texto) {
        return entityManager.persist(ObservacionEvaluacionJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfilId(evaluacionFichaPerfil)
                .observacion(texto)
                .build());
    }

    private EstadoEvaluacionJpaEntity persistirEstado(String id) {
        return entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(id)
                .nombre(id)
                .descripcion("")
                .build());
    }

    private EvaluacionFichaPerfilJpaEntity persistirEvaluacion() {
        return entityManager.persist(EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .representanteComiteId(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .fechaCreacion(Instant.now())
                .build());
    }

    private void persistirEstadoEvaluacionFicha(EvaluacionFichaPerfilJpaEntity evaluacion,
                                                EstadoEvaluacionJpaEntity estado, Instant fechaActualizacion) {
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(estado)
                .fechaActualizacion(fechaActualizacion)
                .build());
    }

    private void sincronizar() {
        entityManager.flush();
        entityManager.clear();
    }
}
