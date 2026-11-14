package com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity.EntregableEntity;
import com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.entity.EntregableJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EntregableCommandOutputAdapterTest {

    @Autowired
    private EntregableCommandRepository entregableCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private EntregableCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableCommandOutputAdapter(entregableCommandRepository, logger);
    }

    @Test
    void debeGuardarYLeerDeVuelta_cuandoEsNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new EntregableEntity(id, proyectoGrado, "Sistema de riego", 2));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(EntregableJpaEntity.class, id);
        assertThat(guardado.getProyecto()).isEqualTo("Sistema de riego");
        assertThat(guardado.getVersion()).isEqualTo(2);
        assertThat(guardado.getProyectoGrado()).isEqualTo(proyectoGrado);
        verify(logger).debug(EntregableKey.LOG_GUARDADO, id);
    }

    @Test
    void debeReportarExistencia_cuandoElIdEstaONoEstaGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(new EntregableEntity(id, UtilUUID.generarNuevoUUID(), "Sistema de riego", 1));
        entityManager.flush();

        // Act
        var guardado = adapter.existePorId(id);
        var ausente = adapter.existePorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(guardado).isTrue();
        assertThat(ausente).isFalse();
    }
}
