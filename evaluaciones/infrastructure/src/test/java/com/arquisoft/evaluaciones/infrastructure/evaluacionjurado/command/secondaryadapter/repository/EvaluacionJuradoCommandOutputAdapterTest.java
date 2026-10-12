package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

// Las tablas evaluacion/estado_evaluacion no tienen @Entity en el lado de comando (esta HU solo lee
// el jurado y el estado, no gestiona EvaluacionJurado/Evaluacion como recursos propios), asi que
// Hibernate no las genera con ddl-auto. Se crean aqui con el mismo DDL de la migracion
// V20260906143120 para poder ejercitar el @Query nativo real con JOIN, tal como exige la skill de
// testing (nada de mockear el repository en una consulta nativa).
@DataJpaTest
class EvaluacionJuradoCommandOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionJuradoCommandRepository repository;

    private EvaluacionJuradoCommandOutputAdapter adapter;

    private AppLogger logger;

    @BeforeEach
    void setUp() {
        logger = mock(AppLogger.class);
        adapter = new EvaluacionJuradoCommandOutputAdapter(repository, logger);
        crearEsquemaDeSoporte();
    }

    private void crearEsquemaDeSoporte() {
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL, nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                MERGE INTO estado_evaluacion (id, nombre, descripcion)
                VALUES ('PENDIENTE', 'Pendiente', 'Pendiente por realizar')
                """).executeUpdate();
        entityManager.createNativeQuery("""
                MERGE INTO estado_evaluacion (id, nombre, descripcion)
                VALUES ('FINALIZADA', 'Finalizada', 'Evaluacion finalizada')
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacionJurado(UUID jurado, String estadoEvaluacion) {
        UUID evaluacion = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion).setParameter(2, UUID.randomUUID())
                .setParameter(3, estadoEvaluacion).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionJurado).setParameter(2, evaluacion).setParameter(3, jurado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionJurado;
    }

    private UUID sembrarEvaluacionCuantitativaJurado(String estadoEvaluacion) {
        UUID evaluacionJurado = sembrarEvaluacionJurado(UUID.randomUUID(), estadoEvaluacion);
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion_cuantitativa_jurado (id, evaluacion_jurado_id, item_id, puntaje) "
                        + "VALUES (?, ?, ?, ?)")
                .setParameter(1, evaluacionCuantitativaJurado).setParameter(2, evaluacionJurado)
                .setParameter(3, UUID.randomUUID()).setParameter(4, 300)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionCuantitativaJurado;
    }

    // H2 entrega las columnas uuid de una consulta nativa como byte[] (PostgreSQL las entrega como
    // UUID), asi que aqui se ejercita la consulta real por su columna de texto y el mapeo de los
    // identificadores se cubre en EstadoEvaluacionJuradoJpaMapperTest.
    @Test
    void debeLeerElEstadoDeLaEvaluacion_cuandoLaEvaluacionJuradoExiste() {
        // Arrange
        UUID evaluacionJurado = sembrarEvaluacionJurado(UUID.randomUUID(), "PENDIENTE");

        // Act
        var estado = repository.buscarEstadoBloqueado(evaluacionJurado);

        // Assert
        assertThat(estado).hasValueSatisfying(encontrado ->
                assertThat(encontrado.getEstado()).isEqualTo("PENDIENTE"));
    }

    @Test
    void debeLeerEstadoFinalizada_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        UUID evaluacionJurado = sembrarEvaluacionJurado(UUID.randomUUID(), "FINALIZADA");

        // Act
        var estado = repository.buscarEstadoBloqueado(evaluacionJurado);

        // Assert
        assertThat(estado).hasValueSatisfying(encontrado ->
                assertThat(encontrado.getEstado()).isEqualTo("FINALIZADA"));
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionJuradoNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerEstadoBloqueado(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeRetornarEstaFinalizadaTrue_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        UUID evaluacionCuantitativaJurado = sembrarEvaluacionCuantitativaJurado("FINALIZADA");

        // Act & Assert
        assertThat(adapter.estaFinalizada(evaluacionCuantitativaJurado)).isTrue();
    }

    @Test
    void debeRetornarEstaFinalizadaFalse_cuandoLaEvaluacionNoEstaFinalizada() {
        // Arrange
        UUID evaluacionCuantitativaJurado = sembrarEvaluacionCuantitativaJurado("PENDIENTE");

        // Act & Assert
        assertThat(adapter.estaFinalizada(evaluacionCuantitativaJurado)).isFalse();
    }

    @Test
    void debeRetornarEstaFinalizadaFalse_cuandoLaEvaluacionCuantitativaJuradoNoExiste() {
        // Act & Assert
        assertThat(adapter.estaFinalizada(UUID.randomUUID())).isFalse();
    }

    @Test
    void debePersistirElLoteCompletoYLoguear_cuandoSeRegistranTodas() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var entidades = List.of(
                new EvaluacionJuradoEntity(UUID.randomUUID(), evaluacion, UUID.randomUUID()),
                new EvaluacionJuradoEntity(UUID.randomUUID(), evaluacion, UUID.randomUUID()),
                new EvaluacionJuradoEntity(UUID.randomUUID(), evaluacion, UUID.randomUUID()));

        // Act
        adapter.registrarTodas(entidades);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(repository.findAllById(entidades.stream().map(EvaluacionJuradoEntity::id).toList()))
                .extracting(fila -> fila.getJurado())
                .containsExactlyInAnyOrderElementsOf(
                        entidades.stream().map(EvaluacionJuradoEntity::jurado).toList());
        verify(logger).debug(EvaluacionJuradoKey.LOG_LOTE_GUARDADO, 3);
    }

    @Test
    void debeDevolverSoloLosJuradosYaVinculadosALaEvaluacion_cuandoSeConsultan() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var otraEvaluacion = UUID.randomUUID();
        var asignado = UUID.randomUUID();
        var asignadoEnOtra = UUID.randomUUID();
        var libre = UUID.randomUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionJuradoEntity(UUID.randomUUID(), evaluacion, asignado),
                new EvaluacionJuradoEntity(UUID.randomUUID(), otraEvaluacion, asignadoEnOtra)));
        entityManager.flush();
        entityManager.clear();

        // Act
        var asignados = adapter.consultarJuradosAsignados(evaluacion, Set.of(asignado, asignadoEnOtra, libre));

        // Assert
        assertThat(asignados).containsExactly(asignado);
    }
}
