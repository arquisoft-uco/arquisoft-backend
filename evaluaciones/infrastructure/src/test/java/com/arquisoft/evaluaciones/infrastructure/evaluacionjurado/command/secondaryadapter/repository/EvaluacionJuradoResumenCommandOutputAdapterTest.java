package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.entity.CriterioItemCualitativoJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class EvaluacionJuradoResumenCommandOutputAdapterTest {

    private static final Instant ELIMINADO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionJuradoCommandRepository repository;

    private EvaluacionJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionJuradoCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    private UUID sembrarJurado(UUID evaluacion, Instant eliminadoEn, BigDecimal nota) {
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionJuradoJpaEntity.builder()
                .id(evaluacionJurado).evaluacion(evaluacion).jurado(UtilUUID.generarNuevoUUID())
                .estado("EVALUADA").eliminadoEn(eliminadoEn).notaAcumulada(nota).build());
        return evaluacionJurado;
    }

    private UUID sembrarItemCualitativo(int peso) {
        var item = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCualitativoJuradoJpaEntity.builder()
                .id(item).nombre("Item " + item).descripcion("Descripción").peso(peso).build());
        return item;
    }

    private UUID sembrarCriterio(String nombre) {
        var criterio = UtilUUID.generarNuevoUUID();
        entityManager.persist(CriterioItemCualitativoJuradoJpaEntity.builder()
                .id(criterio).nombre(nombre).descripcion("Descripción").build());
        return criterio;
    }

    private UUID sembrarItemCuantitativo(int valor) {
        var item = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCuantitativoJuradoJpaEntity.builder()
                .id(item).nombre("Item " + item).descripcion("Descripción")
                .categoriaId(UtilUUID.generarNuevoUUID()).valor(valor).build());
        return item;
    }

    @Test
    void debeLeerPesoYNombreDelCriterioSoloDeLaEvaluacionPedida_cuandoSeConsultanLosCualitativos() {
        // Arrange
        var evaluacionJurado = sembrarJurado(UtilUUID.generarNuevoUUID(), null, null);
        var otra = sembrarJurado(UtilUUID.generarNuevoUUID(), null, null);
        var itemCumple = sembrarItemCualitativo(3);
        var itemNoCumple = sembrarItemCualitativo(5);
        var cumple = sembrarCriterio("Cumple");
        var noCumple = sembrarCriterio("No cumple");
        entityManager.persist(EvaluacionCualitativaJuradoJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionJurado(evaluacionJurado).item(itemCumple).criterio(cumple).build());
        entityManager.persist(EvaluacionCualitativaJuradoJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionJurado(evaluacionJurado).item(itemNoCumple).criterio(noCumple).build());
        entityManager.persist(EvaluacionCualitativaJuradoJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionJurado(otra).item(itemCumple).criterio(noCumple).build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var calificados = adapter.consultarCualitativosCalificados(evaluacionJurado);

        // Assert
        assertThat(calificados).containsExactlyInAnyOrder(
                new ItemCualitativoCalificadoEntity(3, "Cumple"),
                new ItemCualitativoCalificadoEntity(5, "No cumple"));
    }

    @Test
    void debeLeerPuntajeYValorMaximoSoloDeLaEvaluacionPedida_cuandoSeConsultanLosCuantitativos() {
        // Arrange
        var evaluacionJurado = sembrarJurado(UtilUUID.generarNuevoUUID(), null, null);
        var otra = sembrarJurado(UtilUUID.generarNuevoUUID(), null, null);
        var item = sembrarItemCuantitativo(100);
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionJuradoId(evaluacionJurado).itemId(item).puntaje(80).build());
        entityManager.persist(EvaluacionCuantitativaJuradoJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionJuradoId(otra).itemId(item).puntaje(10).build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var calificados = adapter.consultarCuantitativosCalificados(evaluacionJurado);

        // Assert
        assertThat(calificados).containsExactly(new ItemCuantitativoCalificadoEntity(80, 100));
    }

    @Test
    void debeGuardarYLimpiarElResumen_cuandoSeActualizaConValoresYConNulos() {
        // Arrange
        var evaluacionJurado = sembrarJurado(UtilUUID.generarNuevoUUID(), null, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarResumen(evaluacionJurado, new ResumenEvaluacionEntity(
                new BigDecimal("1.00"), new BigDecimal("2.00"), new BigDecimal("3.00")));
        var guardado = repository.findById(evaluacionJurado).orElseThrow();
        var notaGuardada = guardado.getNotaAcumulada();
        adapter.actualizarResumen(evaluacionJurado, new ResumenEvaluacionEntity(null, null, null));
        var limpio = repository.findById(evaluacionJurado).orElseThrow();

        // Assert
        assertThat(notaGuardada).isEqualByComparingTo("3.00");
        assertThat(guardado.getResumenCualitativo()).isEqualByComparingTo("1.00");
        assertThat(guardado.getResumenCuantitativo()).isEqualByComparingTo("2.00");
        assertThat(limpio.getNotaAcumulada()).isNull();
        assertThat(limpio.getResumenCualitativo()).isNull();
        assertThat(limpio.getResumenCuantitativo()).isNull();
    }

    @Test
    void debeDevolverSoloResumenesConNotaDeJuradosVigentes_cuandoSeConsultaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var conNota = sembrarJurado(evaluacion, null, new BigDecimal("4.00"));
        sembrarJurado(evaluacion, null, null);
        sembrarJurado(evaluacion, ELIMINADO_EN, new BigDecimal("1.00"));
        sembrarJurado(otra, null, new BigDecimal("2.00"));
        entityManager.flush();
        entityManager.clear();
        adapter.actualizarResumen(conNota, new ResumenEvaluacionEntity(
                new BigDecimal("4.00"), new BigDecimal("4.00"), new BigDecimal("4.00")));

        // Act
        var resumenes = adapter.consultarResumenesPorEvaluacion(evaluacion);

        // Assert
        assertThat(resumenes).singleElement().satisfies(resumen ->
                assertThat(resumen.notaAcumulada()).isEqualByComparingTo("4.00"));
    }

    @Test
    void debeNoEstarCompletaNiLeerElContexto_cuandoElJuradoFueRetirado() {
        // Arrange
        var retirado = sembrarJurado(UtilUUID.generarNuevoUUID(), ELIMINADO_EN, null);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.estaCompleta(retirado)).isFalse();
        assertThat(repository.buscarEstadoBloqueado(retirado)).isEmpty();
        assertThat(repository.buscarContextoBloqueado(retirado)).isEmpty();
    }
}
