package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoEntregableJpaEntity;
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
    private ArtefactoEntregableCommandRepository artefactoEntregableCommandRepository;

    @Autowired
    private ArtefactoEntregableQueryRepository artefactoEntregableQueryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ArtefactoEntregableCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoEntregableCommandOutputAdapter(artefactoEntregableCommandRepository, logger);
    }

    private static ArtefactoEntregableEntity entity(UUID entregable, UUID version, UUID artefacto,
                                                    String documento, String ruta, int numeroVersion) {
        return new ArtefactoEntregableEntity(UtilUUID.generarNuevoUUID(), version, entregable,
                ruta, artefacto, numeroVersion, UtilUUID.generarNuevoUUID(), documento, UtilUUID.generarNuevoUUID(),
                "Repo");
    }

    @Test
    void debeGuardarLaFotoConSuUriYLosDatosDelArtefacto_cuandoHayItems() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var primero = entity(entregable, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Informe", "01-Informe/v3-informe.pdf", 3);
        var segundo = entity(entregable, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Anexo", "02-Anexo/v1-anexo.pdf", 1);

        // Act
        adapter.vincular(List.of(primero, segundo));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var foto = entityManager.find(ArtefactoEntregableJpaEntity.class, primero.id());
        assertThat(foto.getArtefacto()).isEqualTo(primero.artefacto());
        assertThat(foto.getVersionArtefacto()).isEqualTo(primero.versionArtefacto());
        assertThat(foto.getNumeroVersion()).isEqualTo(3);
        assertThat(foto.getNombreDocumento()).isEqualTo("Informe");
        assertThat(foto.getVersionRepositorioArtefacto()).isEqualTo(primero.versionRepositorioArtefacto());
        assertThat(foto.getRepositorio()).isEqualTo(primero.repositorio());
        assertThat(foto.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(foto.getEntregableId()).isEqualTo(entregable);
        assertThat(foto.getUriArchivo()).isEqualTo("01-Informe/v3-informe.pdf");
        assertThat(entityManager.find(ArtefactoEntregableJpaEntity.class, segundo.id())).isNotNull();
        verify(logger).debug(ArtefactoEntregableKey.LOG_GUARDADOS, entregable, 2);
    }

    @Test
    void debeDevolverLaVersionEmpaquetada_aunqueElArtefactoHayaAvanzado() {
        // Arrange
        var cancelado = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var versionVieja = UtilUUID.generarNuevoUUID();
        var versionNueva = UtilUUID.generarNuevoUUID();
        var delCancelado = entity(cancelado, versionVieja, artefacto, "Informe", "01-Informe/v1-v1.pdf", 1);
        var delNuevo = entity(nuevo, versionNueva, artefacto, "Informe", "01-Informe/v2-v2.pdf", 2);
        adapter.vincular(List.of(delCancelado));
        adapter.vincular(List.of(delNuevo));
        entityManager.flush();
        entityManager.clear();
        var consulta = new ArtefactoEntregableQueryOutputAdapter(artefactoEntregableQueryRepository);

        // Act
        var delEntregableCancelado = consulta.consultarPorEntregable(cancelado);
        var delEntregableNuevo = consulta.consultarPorEntregable(nuevo);

        // Assert
        assertThat(delEntregableCancelado).singleElement().satisfies(readModel -> {
            assertThat(readModel.versionArtefacto()).isEqualTo(versionVieja);
            assertThat(readModel.numeroVersion()).isEqualTo(1);
            assertThat(readModel.uriArchivo()).isEqualTo("01-Informe/v1-v1.pdf");
            assertThat(readModel.artefacto()).isEqualTo(artefacto);
        });
        assertThat(delEntregableNuevo).singleElement().satisfies(readModel -> {
            assertThat(readModel.versionArtefacto()).isEqualTo(versionNueva);
            assertThat(readModel.numeroVersion()).isEqualTo(2);
            assertThat(readModel.uriArchivo()).isEqualTo("01-Informe/v2-v2.pdf");
            assertThat(readModel.artefacto()).isEqualTo(artefacto);
        });
    }
}
