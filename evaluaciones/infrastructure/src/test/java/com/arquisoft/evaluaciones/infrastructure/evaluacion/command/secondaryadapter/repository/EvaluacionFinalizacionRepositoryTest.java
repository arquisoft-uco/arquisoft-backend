package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.entity.EntregableJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.entity.ObservacionItemAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// H2 entrega las columnas uuid de una consulta nativa como byte[]: de buscarAvance se leen solo las
// columnas escalares, y el mapeo de identificadores lo cubre el adaptador contra PostgreSQL.
@DataJpaTest
class EvaluacionFinalizacionRepositoryTest {

    private static final Instant ELIMINADO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionCommandRepository repository;

    private UUID sembrarEvaluacion(String proyecto, int version) {
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(EntregableJpaEntity.builder()
                .id(entregable).proyecto(proyecto).version(version)
                .proyectoGrado(UtilUUID.generarNuevoUUID()).build());
        entityManager.persist(EvaluacionJpaEntity.builder()
                .id(evaluacion).entregable(entregable).estadoEvaluacion("EN_PROGRESO").build());
        return evaluacion;
    }

    private UUID sembrarJurado(UUID evaluacion, String estado, Instant eliminadoEn) {
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionJuradoJpaEntity.builder()
                .id(evaluacionJurado).evaluacion(evaluacion).jurado(UtilUUID.generarNuevoUUID())
                .estado(estado).eliminadoEn(eliminadoEn).build());
        return evaluacionJurado;
    }

    private UUID sembrarAsesor(UUID evaluacion, BigDecimal notaAcumulada) {
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(evaluacionAsesor).evaluacion(evaluacion).asesor(UtilUUID.generarNuevoUUID())
                .notaAcumulada(notaAcumulada).build());
        return evaluacionAsesor;
    }

    private void sembrarObservacionDelJurado(UUID evaluacionJurado) {
        var cuantitativa = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(cuantitativa).evaluacionJuradoId(evaluacionJurado)
                .itemId(UtilUUID.generarNuevoUUID()).puntaje(10).build());
        entityManager.persist(ObservacionItemJuradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).evaluacionCuantitativaJuradoId(cuantitativa)
                .descripcion("Observación").build());
    }

    private void sembrarObservacionDelAsesor(UUID evaluacionAsesor) {
        var cuantitativa = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionCuantitativaAsesorJpaEntity.builder()
                .id(cuantitativa).evaluacionAsesor(evaluacionAsesor)
                .item(UtilUUID.generarNuevoUUID()).puntaje(10).build());
        entityManager.persist(ObservacionItemAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).evaluacionCuantitativaAsesorId(cuantitativa)
                .descripcion("Observación").build());
    }

    @Test
    void debeContarSoloJuradosVigentesYEvaluados_cuandoHayUnoRetiradoYAsesorConNota() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto Alfa", 3);
        sembrarJurado(evaluacion, "EVALUADA", null);
        sembrarJurado(evaluacion, "PENDIENTE_POR_EVALUAR", null);
        sembrarJurado(evaluacion, "EVALUADA", ELIMINADO_EN);
        sembrarAsesor(evaluacion, new BigDecimal("4.00"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var avance = repository.buscarAvance(evaluacion);

        // Assert
        assertThat(avance).hasValueSatisfying(encontrado -> {
            assertThat(encontrado.getEstado()).isEqualTo("EN_PROGRESO");
            assertThat(encontrado.getProyecto()).isEqualTo("Proyecto Alfa");
            assertThat(encontrado.getVersion()).isEqualTo(3);
            assertThat(encontrado.getJuradosVigentes()).isEqualTo(2);
            assertThat(encontrado.getJuradosEvaluados()).isEqualTo(1);
            assertThat(encontrado.getAsesorCompleto()).isTrue();
        });
    }

    @Test
    void debeMarcarAsesorIncompleto_cuandoSuNotaAcumuladaEsNula() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto Beta", 1);
        sembrarAsesor(evaluacion, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var avance = repository.buscarAvance(evaluacion);

        // Assert
        assertThat(avance).hasValueSatisfying(encontrado -> {
            assertThat(encontrado.getAsesorCompleto()).isFalse();
            assertThat(encontrado.getJuradosVigentes()).isZero();
        });
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionNoExiste() {
        // Act & Assert
        assertThat(repository.buscarAvance(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeDetectarObservaciones_cuandoElAsesorTieneUna() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto", 1);
        sembrarObservacionDelAsesor(sembrarAsesor(evaluacion, new BigDecimal("4.00")));
        entityManager.flush();

        // Act & Assert
        assertThat(repository.existenObservaciones(evaluacion)).isTrue();
    }

    @Test
    void debeDetectarObservaciones_cuandoUnJuradoVigenteTieneUna() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto", 1);
        sembrarObservacionDelJurado(sembrarJurado(evaluacion, "EVALUADA", null));
        entityManager.flush();

        // Act & Assert
        assertThat(repository.existenObservaciones(evaluacion)).isTrue();
    }

    @Test
    void debeIgnorarLaObservacionDeUnJuradoRetiradoYLasDeOtraEvaluacion_cuandoNoHayOtras() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto", 1);
        var otra = sembrarEvaluacion("Otro", 1);
        sembrarAsesor(evaluacion, new BigDecimal("4.00"));
        sembrarObservacionDelJurado(sembrarJurado(evaluacion, "EVALUADA", ELIMINADO_EN));
        sembrarObservacionDelJurado(sembrarJurado(otra, "EVALUADA", null));
        entityManager.flush();

        // Act & Assert
        assertThat(repository.existenObservaciones(evaluacion)).isFalse();
    }

    @Test
    void debeGuardarNotasPesosYCantidad_cuandoSeConsolida() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto", 1);
        entityManager.flush();
        entityManager.clear();

        // Act
        var filas = repository.actualizarNotas(evaluacion, new BigDecimal("4.00"), new BigDecimal("3.00"),
                new BigDecimal("0.3333"), new BigDecimal("0.6667"), 2);

        // Assert
        assertThat(filas).isEqualTo(1);
        assertThat(repository.findById(evaluacion)).hasValueSatisfying(fila -> {
            assertThat(fila.getNotaAsesor()).isEqualByComparingTo("4.00");
            assertThat(fila.getNotaJurados()).isEqualByComparingTo("3.00");
            assertThat(fila.getPesoAsesor()).isEqualByComparingTo("0.3333");
            assertThat(fila.getPesoJurados()).isEqualByComparingTo("0.6667");
            assertThat(fila.getCantidadJurados()).isEqualTo(2);
            assertThat(fila.getEstadoEvaluacion()).isEqualTo("EN_PROGRESO");
        });
    }

    @Test
    void debeFinalizarConNotaYResultado_cuandoSeCierraLaEvaluacion() {
        // Arrange
        var evaluacion = sembrarEvaluacion("Proyecto", 1);
        var otra = sembrarEvaluacion("Otro", 1);
        entityManager.flush();
        entityManager.clear();

        // Act
        var filas = repository.finalizar(evaluacion, "FINALIZADA", new BigDecimal("3.25"), "APROBADA");

        // Assert
        assertThat(filas).isEqualTo(1);
        assertThat(repository.findById(evaluacion)).hasValueSatisfying(fila -> {
            assertThat(fila.getEstadoEvaluacion()).isEqualTo("FINALIZADA");
            assertThat(fila.getNotaFinal()).isEqualByComparingTo("3.25");
            assertThat(fila.getResultadoEvaluacion()).isEqualTo("APROBADA");
        });
        assertThat(repository.findById(otra)).hasValueSatisfying(fila ->
                assertThat(fila.getEstadoEvaluacion()).isEqualTo("EN_PROGRESO"));
    }
}
