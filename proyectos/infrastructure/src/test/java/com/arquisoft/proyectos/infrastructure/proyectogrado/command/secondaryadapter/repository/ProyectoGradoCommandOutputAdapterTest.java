package com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private ProyectoGradoCommandRepository proyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoCommandOutputAdapter(proyectoGradoCommandRepository, logger);
    }

    private ProyectoGradoEntity proyecto(UUID fichaPerfil) {
        return new ProyectoGradoEntity(UUID.randomUUID(), fichaPerfil, "Sistema de gestión", UUID.randomUUID(),
                "EN_PROCESO");
    }

    @Test
    void debePersistirYLeerTodosLosCampos_cuandoSeRegistraElProyecto() {
        // Arrange
        var registrado = proyecto(UUID.randomUUID());

        // Act
        adapter.registrar(registrado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorId(registrado.id())).contains(registrado);
        verify(logger).debug(ProyectoGradoKey.LOG_GUARDADO, registrado.id(), registrado.fichaPerfil());
    }

    @Test
    void debeResponderSiLaFichaYaTieneProyecto_cuandoSeConsultaPorFichaPerfil() {
        // Arrange
        var conProyecto = UUID.randomUUID();
        adapter.registrar(proyecto(conProyecto));
        entityManager.flush();

        // Act
        var existe = adapter.existePorFichaPerfil(conProyecto);
        var noExiste = adapter.existePorFichaPerfil(UUID.randomUUID());

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerPorId(UUID.randomUUID())).isEmpty();
    }
}
