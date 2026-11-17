package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(EvaluacionCualitativaAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class EvaluacionCualitativaAsesorCommandOutputAdapterTest {

    @Autowired
    private EvaluacionCualitativaAsesorCommandOutputAdapter adapter;

    @Autowired
    private EvaluacionCualitativaAsesorCommandRepository repository;

    @MockitoBean
    private AppLogger logger;

    @Test
    void debePersistirElLote_cuandoRegistraTodas() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entidades = List.of(
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()),
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));

        // Act
        adapter.registrarTodas(entidades);
        repository.flush();

        // Assert
        var guardadas = repository.findAll();
        assertThat(guardadas).hasSize(2);
        assertThat(guardadas).extracting(e -> e.getId())
                .containsExactlyInAnyOrder(entidades.get(0).id(), entidades.get(1).id());
        assertThat(guardadas).extracting(e -> e.getEvaluacionAsesor()).containsOnly(evaluacionAsesor);
        verify(logger).debug(any(ClaveMensaje.class), eq(2));
    }

    @Test
    void debeRetornarSoloItemsDeEsaEvaluacion_cuandoConsultaItemsRegistrados() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var otraEvaluacion = UtilUUID.generarNuevoUUID();
        var itemRegistrado = UtilUUID.generarNuevoUUID();
        var itemDeOtraEvaluacion = UtilUUID.generarNuevoUUID();
        var itemSinRegistrar = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemRegistrado, UtilUUID.generarNuevoUUID()),
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), otraEvaluacion,
                        itemDeOtraEvaluacion, UtilUUID.generarNuevoUUID())));
        repository.flush();

        // Act
        var resultado = adapter.consultarItemsRegistrados(
                evaluacionAsesor, Set.of(itemRegistrado, itemDeOtraEvaluacion, itemSinRegistrar));

        // Assert
        assertThat(resultado).containsExactly(itemRegistrado);
    }

    @Test
    void debeDetectarSoloElItemUsado_cuandoConsultaExistenciaPorItem() {
        // Arrange
        var itemUsado = UtilUUID.generarNuevoUUID();
        var itemSinUso = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                        itemUsado, UtilUUID.generarNuevoUUID())));
        repository.flush();

        // Act
        var usado = adapter.existePorItem(itemUsado);
        var sinUso = adapter.existePorItem(itemSinUso);

        // Assert
        assertThat(usado).isTrue();
        assertThat(sinUso).isFalse();
    }

    @Test
    void debeEliminarSoloItemsDelLote_cuandoHayOtrosItems() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var itemOmitido1 = UtilUUID.generarNuevoUUID();
        var itemOmitido2 = UtilUUID.generarNuevoUUID();
        var itemConservado = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemOmitido1, UtilUUID.generarNuevoUUID()),
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemOmitido2, UtilUUID.generarNuevoUUID()),
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemConservado, UtilUUID.generarNuevoUUID())));
        repository.flush();

        // Act
        adapter.eliminarPorItems(evaluacionAsesor, Set.of(itemOmitido1, itemOmitido2));

        // Assert
        assertThat(repository.findAll()).extracting(e -> e.getItem()).containsExactly(itemConservado);
        verify(logger).debug(any(ClaveMensaje.class), eq(2));
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(new EvaluacionCualitativaAsesorEntity(id, evaluacionAsesor, item, criterio)));
        repository.flush();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).contains(new EvaluacionCualitativaAsesorEntity(id, evaluacionAsesor, item, criterio));
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarSoloElCriterio() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterioNuevo = UtilUUID.generarNuevoUUID();
        var otra = new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        adapter.registrarTodas(List.of(
                new EvaluacionCualitativaAsesorEntity(id, evaluacionAsesor, item, UtilUUID.generarNuevoUUID()),
                otra));
        repository.flush();

        // Act
        adapter.actualizar(new EvaluacionCualitativaAsesorEntity(id, evaluacionAsesor, item, criterioNuevo));
        repository.flush();

        // Assert
        assertThat(adapter.obtenerPorId(id)).contains(
                new EvaluacionCualitativaAsesorEntity(id, evaluacionAsesor, item, criterioNuevo));
        assertThat(adapter.obtenerPorId(otra.id())).contains(otra);
        assertThat(repository.findAll()).hasSize(2);
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void noDebeTocarOtraEvaluacionAsesor_cuandoMismoItem() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var otraEvaluacion = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        item, UtilUUID.generarNuevoUUID()),
                new EvaluacionCualitativaAsesorEntity(UtilUUID.generarNuevoUUID(), otraEvaluacion,
                        item, UtilUUID.generarNuevoUUID())));
        repository.flush();

        // Act
        adapter.eliminarPorItems(evaluacionAsesor, Set.of(item));

        // Assert
        assertThat(repository.findAll()).extracting(e -> e.getEvaluacionAsesor()).containsExactly(otraEvaluacion);
    }
}
