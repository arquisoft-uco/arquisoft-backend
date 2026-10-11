package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProyectoGradoAccesoQueryOutputAdapterTest {

    @Autowired
    private ProyectoGradoAccesoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private ProyectoGradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoAccesoQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarCoordinador_cuandoProyectoExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(proyectoGrado, "EN_PROCESO", coordinador,
                UtilUUID.generarNuevoUUID(), "Sistema de gestion"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerCoordinadorPorId(proyectoGrado);

        // Assert
        assertThat(resultado).contains(coordinador);
    }

    @Test
    void debeRetornarVacio_cuandoProyectoNoExiste() {
        // Act
        var resultado = adapter.obtenerCoordinadorPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
