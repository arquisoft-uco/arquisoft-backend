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
}
