package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoEntregableJpaEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.ArtefactoEntregableQueryOutputAdapter;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.ArtefactoEntregableQueryRepository;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ArtefactoEntregableCommandOutputAdapterTest {

    @Autowired
    private ArtefactoCommandRepository artefactoCommandRepository;

    @Autowired
    private ArtefactoEntregableCommandRepository artefactoEntregableCommandRepository;

    @Autowired
    private ArtefactoEntregableQueryRepository artefactoEntregableQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ArtefactoEntregableCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoEntregableCommandOutputAdapter(
                artefactoCommandRepository, artefactoEntregableCommandRepository, logger);
    }

    private static ArtefactoEntregableEntity entity(UUID entregable, UUID artefacto, UUID versionRepositorio,
                                                    String documento, String ruta) {
        return new ArtefactoEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), entregable,
                ruta, artefacto, versionRepositorio, documento, UtilUUID.generarNuevoUUID(), "Repo");
    }

    @Test
    void debeGuardarArtefactoYVinculo_cuandoHayItems() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var primero = entity(entregable, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Informe", "01-Informe/v3-informe.pdf");
        var segundo = entity(entregable, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Anexo", "02-Anexo/v1-anexo.pdf");

        // Act
        adapter.guardarTodos(List.of(primero, segundo));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var artefacto = entityManager.find(ArtefactoJpaEntity.class, primero.versionArtefacto());
        assertThat(artefacto.getUriArchivo()).isEqualTo("01-Informe/v3-informe.pdf");
        assertThat(artefacto.getArtefacto()).isEqualTo(primero.artefacto());
        assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(primero.versionRepositorioArtefacto());
        assertThat(artefacto.getNombreDocumento()).isEqualTo("Informe");
        assertThat(artefacto.getRepositorio()).isEqualTo(primero.repositorio());
        assertThat(artefacto.getNombreRepositorio()).isEqualTo("Repo");
        var vinculo = entityManager.find(ArtefactoEntregableJpaEntity.class, primero.id());
        assertThat(vinculo.getArtefactoId()).isEqualTo(primero.versionArtefacto());
        assertThat(vinculo.getEntregableId()).isEqualTo(entregable);
        assertThat(entityManager.find(ArtefactoEntregableJpaEntity.class, segundo.id())).isNotNull();
        verify(logger).debug(ArtefactoEntregableKey.LOG_GUARDADOS, entregable, 2);
    }

    @Test
    void debeConservarUnaFotografiaPorEntregable_cuandoElMismoArtefactoEntraEnDosEntregables() {
        // Arrange
        var cancelado = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var versionRepositorio = UtilUUID.generarNuevoUUID();
        var delCancelado = entity(cancelado, artefacto, versionRepositorio, "Informe", "01-Informe/v1-v1.pdf");
        var delNuevo = entity(nuevo, artefacto, versionRepositorio, "Informe", "01-Informe/v1-v2.pdf");
        adapter.guardarTodos(List.of(delCancelado));
        adapter.guardarTodos(List.of(delNuevo));
        entityManager.flush();
        entityManager.clear();
        var consulta = new ArtefactoEntregableQueryOutputAdapter(artefactoEntregableQueryRepository);

        // Act
        var delEntregableCancelado = consulta.consultarPorEntregable(cancelado);
        var delEntregableNuevo = consulta.consultarPorEntregable(nuevo);

        // Assert
        assertThat(delEntregableCancelado).singleElement().satisfies(readModel -> {
            assertThat(readModel.versionArtefacto()).isEqualTo(delCancelado.versionArtefacto());
            assertThat(readModel.uriArchivo()).isEqualTo("01-Informe/v1-v1.pdf");
            assertThat(readModel.artefacto()).isEqualTo(artefacto);
        });
        assertThat(delEntregableNuevo).singleElement().satisfies(readModel -> {
            assertThat(readModel.versionArtefacto()).isEqualTo(delNuevo.versionArtefacto());
            assertThat(readModel.uriArchivo()).isEqualTo("01-Informe/v1-v2.pdf");
            assertThat(readModel.artefacto()).isEqualTo(artefacto);
        });
        assertThat(delCancelado.versionArtefacto()).isNotEqualTo(delNuevo.versionArtefacto());
    }
}
