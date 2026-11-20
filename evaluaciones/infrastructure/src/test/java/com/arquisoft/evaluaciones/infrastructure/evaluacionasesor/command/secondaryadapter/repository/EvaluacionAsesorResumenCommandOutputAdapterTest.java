package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.entity.CriterioItemCualitativoAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.entity.EvaluacionCualitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class EvaluacionAsesorResumenCommandOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EvaluacionAsesorCommandRepository repository;

    private EvaluacionAsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsesorCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    private UUID sembrarEvaluacionAsesor(UUID evaluacion) {
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        entityManager.persist(EvaluacionAsesorJpaEntity.builder()
                .id(evaluacionAsesor).evaluacion(evaluacion).asesor(UtilUUID.generarNuevoUUID()).build());
        return evaluacionAsesor;
    }

    private UUID sembrarItemCualitativo(int peso) {
        var item = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCualitativoAsesorJpaEntity.builder()
                .id(item).nombre("Item " + item).descripcion("Descripción").peso(peso).build());
        return item;
    }

    private UUID sembrarCriterio(String nombre) {
        var criterio = UtilUUID.generarNuevoUUID();
        entityManager.persist(CriterioItemCualitativoAsesorJpaEntity.builder()
                .id(criterio).nombre(nombre).descripcion("Descripción").build());
        return criterio;
    }

    private UUID sembrarItemCuantitativo(int valor) {
        var item = UtilUUID.generarNuevoUUID();
        entityManager.persist(ItemCuantitativoAsesorJpaEntity.builder()
                .id(item).nombre("Item " + item).descripcion("Descripción")
                .categoria(UtilUUID.generarNuevoUUID()).valor(valor).build());
        return item;
    }

    private void evaluarCualitativo(UUID evaluacionAsesor, UUID item, UUID criterio) {
        entityManager.persist(EvaluacionCualitativaAsesorJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionAsesor(evaluacionAsesor).item(item).criterio(criterio).build());
    }

    private void evaluarCuantitativo(UUID evaluacionAsesor, UUID item, int puntaje) {
        entityManager.persist(EvaluacionCuantitativaAsesorJpaEntity.builder().id(UtilUUID.generarNuevoUUID())
                .evaluacionAsesor(evaluacionAsesor).item(item).puntaje(puntaje).build());
    }

    @Test
    void debeEstarCompleta_cuandoTodosLosItemsDeAmbosCatalogosTienenEvaluacion() {
        // Arrange
        var evaluacionAsesor = sembrarEvaluacionAsesor(UtilUUID.generarNuevoUUID());
        evaluarCualitativo(evaluacionAsesor, sembrarItemCualitativo(1), sembrarCriterio("Cumple"));
        evaluarCuantitativo(evaluacionAsesor, sembrarItemCuantitativo(100), 90);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.estaCompleta(evaluacionAsesor)).isTrue();
    }

    @Test
    void debeNoEstarCompleta_cuandoFaltaUnItemCuantitativoOLasEvaluacionesSonDeOtroAsesor() {
        // Arrange
        var evaluacionAsesor = sembrarEvaluacionAsesor(UtilUUID.generarNuevoUUID());
        var otro = sembrarEvaluacionAsesor(UtilUUID.generarNuevoUUID());
        var itemCualitativo = sembrarItemCualitativo(1);
        var criterio = sembrarCriterio("Cumple");
        var itemCuantitativo = sembrarItemCuantitativo(100);
        evaluarCualitativo(evaluacionAsesor, itemCualitativo, criterio);
        evaluarCualitativo(otro, itemCualitativo, criterio);
        evaluarCuantitativo(otro, itemCuantitativo, 90);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.estaCompleta(evaluacionAsesor)).isFalse();
    }

    @Test
    void debeLeerLosItemsCalificadosSoloDelAsesorPedido_cuandoSeConsultanAmbosCatalogos() {
        // Arrange
        var evaluacionAsesor = sembrarEvaluacionAsesor(UtilUUID.generarNuevoUUID());
        var otro = sembrarEvaluacionAsesor(UtilUUID.generarNuevoUUID());
        var itemCualitativo = sembrarItemCualitativo(4);
        var itemCuantitativo = sembrarItemCuantitativo(50);
        var noCumple = sembrarCriterio("No cumple");
        evaluarCualitativo(evaluacionAsesor, itemCualitativo, noCumple);
        evaluarCualitativo(otro, itemCualitativo, noCumple);
        evaluarCuantitativo(evaluacionAsesor, itemCuantitativo, 30);
        evaluarCuantitativo(otro, itemCuantitativo, 5);
        entityManager.flush();
        entityManager.clear();

        // Act
        var cualitativos = adapter.consultarCualitativosCalificados(evaluacionAsesor);
        var cuantitativos = adapter.consultarCuantitativosCalificados(evaluacionAsesor);

        // Assert
        assertThat(cualitativos).containsExactly(new ItemCualitativoCalificadoEntity(4, "No cumple"));
        assertThat(cuantitativos).containsExactly(new ItemCuantitativoCalificadoEntity(30, 50));
    }

    @Test
    void debeGuardarLeerYLimpiarElResumen_cuandoSeActualizaYSeConsultaPorEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarEvaluacionAsesor(evaluacion);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarResumen(evaluacionAsesor, new ResumenEvaluacionEntity(
                new BigDecimal("1.00"), new BigDecimal("2.00"), new BigDecimal("3.00")));
        var calculado = adapter.obtenerResumenPorEvaluacion(evaluacion);
        adapter.actualizarResumen(evaluacionAsesor, new ResumenEvaluacionEntity(null, null, null));
        var limpio = adapter.obtenerResumenPorEvaluacion(evaluacion);

        // Assert
        assertThat(calculado).hasValueSatisfying(resumen -> {
            assertThat(resumen.resumenCualitativo()).isEqualByComparingTo("1.00");
            assertThat(resumen.resumenCuantitativo()).isEqualByComparingTo("2.00");
            assertThat(resumen.notaAcumulada()).isEqualByComparingTo("3.00");
        });
        assertThat(limpio).hasValueSatisfying(resumen -> assertThat(resumen.notaAcumulada()).isNull());
        assertThat(adapter.obtenerResumenPorEvaluacion(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
