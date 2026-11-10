package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PaqueteEntregableAccesoQueryOutputAdapter.class)
class PaqueteEntregableAccesoQueryOutputAdapterTest {

    @Autowired
    private PaqueteEntregableAccesoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeObtenerPaquete_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(PaqueteEntregableJpaEntity.builder()
                .id(id).entregableProyectoGradoId(entregable).bucket("bucket")
                .claveObjeto("paquetes/x.zip").nombreArchivo("p.zip").tamanoBytes(5L)
                .generadoEn(Instant.now()).build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorEntregable(entregable);

        // Assert
        assertThat(resultado).hasValueSatisfying(paquete -> {
            assertThat(paquete.id()).isEqualTo(id);
            assertThat(paquete.entregableProyectoGrado()).isEqualTo(entregable);
            assertThat(paquete.bucket()).isEqualTo("bucket");
            assertThat(paquete.claveObjeto()).isEqualTo("paquetes/x.zip");
            assertThat(paquete.nombreArchivo()).isEqualTo("p.zip");
        });
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorEntregable(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
