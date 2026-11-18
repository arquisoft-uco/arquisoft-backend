package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;
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
@Import(EvaluacionCuantitativaAsesorCommandOutputAdapter.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class EvaluacionCuantitativaAsesorCommandOutputAdapterTest {

    @Autowired
    private EvaluacionCuantitativaAsesorCommandOutputAdapter adapter;

    @Autowired
    private EvaluacionCuantitativaAsesorCommandRepository repository;

    @MockitoBean
    private AppLogger logger;

    @Test
    void debePersistirElLote_cuandoRegistraTodas() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entidades = List.of(
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        UtilUUID.generarNuevoUUID(), 10),
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        UtilUUID.generarNuevoUUID(), 20));

        // Act
        adapter.registrarTodas(entidades);
        repository.flush();

        // Assert
        var guardadas = repository.findAll();
        assertThat(guardadas).hasSize(2);
        assertThat(guardadas).extracting(e -> e.getId())
                .containsExactlyInAnyOrder(entidades.get(0).id(), entidades.get(1).id());
        assertThat(guardadas).extracting(e -> e.getEvaluacionAsesor()).containsOnly(evaluacionAsesor);
        assertThat(guardadas).extracting(e -> e.getPuntaje()).containsExactlyInAnyOrder(10, 20);
        verify(logger).debug(any(ClaveMensaje.class), eq(2));
    }

    @Test
    void debeEliminarSoloItemsDelLote_cuandoHayOtrosItems() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var itemOmitido1 = UtilUUID.generarNuevoUUID();
        var itemOmitido2 = UtilUUID.generarNuevoUUID();
        var itemConservado = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemOmitido1, 10),
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemOmitido2, 10),
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemConservado, 10)));
        repository.flush();

        // Act
        adapter.eliminarPorItems(evaluacionAsesor, Set.of(itemOmitido1, itemOmitido2));

        // Assert
        assertThat(repository.findAll()).extracting(e -> e.getItem()).containsExactly(itemConservado);
        verify(logger).debug(any(ClaveMensaje.class), eq(2));
    }

    @Test
    void noDebeTocarOtraEvaluacionAsesor_cuandoMismoItem() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var otraEvaluacion = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor, item, 10),
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), otraEvaluacion, item, 10)));
        repository.flush();

        // Act
        adapter.eliminarPorItems(evaluacionAsesor, Set.of(item));

        // Assert
        assertThat(repository.findAll()).extracting(e -> e.getEvaluacionAsesor()).containsExactly(otraEvaluacion);
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
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), evaluacionAsesor,
                        itemRegistrado, 10),
                new EvaluacionCuantitativaAsesorEntity(UtilUUID.generarNuevoUUID(), otraEvaluacion,
                        itemDeOtraEvaluacion, 10)));
        repository.flush();

        // Act
        var resultado = adapter.consultarItemsRegistrados(
                evaluacionAsesor, Set.of(itemRegistrado, itemDeOtraEvaluacion, itemSinRegistrar));

        // Assert
        assertThat(resultado).containsExactly(itemRegistrado);
    }

    @Test
    void debeActualizarSoloElPuntajeDeLaFila_cuandoExiste() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var objetivo = UtilUUID.generarNuevoUUID();
        var itemObjetivo = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var itemOtra = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCuantitativaAsesorEntity(objetivo, evaluacionAsesor, itemObjetivo, 10),
                new EvaluacionCuantitativaAsesorEntity(otra, evaluacionAsesor, itemOtra, 20)));
        repository.flush();

        // Act
        adapter.actualizarPuntaje(objetivo, 450);

        // Assert
        var filas = repository.findAll();
        var actualizada = filas.stream().filter(e -> e.getId().equals(objetivo)).findFirst().orElseThrow();
        var intacta = filas.stream().filter(e -> e.getId().equals(otra)).findFirst().orElseThrow();
        assertThat(actualizada.getPuntaje()).isEqualTo(450);
        assertThat(actualizada.getItem()).isEqualTo(itemObjetivo);
        assertThat(actualizada.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(intacta.getPuntaje()).isEqualTo(20);
        verify(logger).debug(any(ClaveMensaje.class), eq(objetivo));
    }

    @Test
    void debeObtenerElItemDeLaFilaPedida_cuandoExisteYVacioCuandoNo() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var pedida = UtilUUID.generarNuevoUUID();
        var itemPedido = UtilUUID.generarNuevoUUID();
        var otra = UtilUUID.generarNuevoUUID();
        var itemOtra = UtilUUID.generarNuevoUUID();
        adapter.registrarTodas(List.of(
                new EvaluacionCuantitativaAsesorEntity(pedida, evaluacionAsesor, itemPedido, 10),
                new EvaluacionCuantitativaAsesorEntity(otra, evaluacionAsesor, itemOtra, 20)));
        repository.flush();

        // Act
        var encontrado = adapter.obtenerItem(pedida);
        var inexistente = adapter.obtenerItem(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).contains(itemPedido);
        assertThat(inexistente).isEmpty();
    }
}
