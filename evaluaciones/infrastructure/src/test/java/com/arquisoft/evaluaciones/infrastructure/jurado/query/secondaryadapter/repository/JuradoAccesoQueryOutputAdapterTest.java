package com.arquisoft.evaluaciones.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JuradoAccesoQueryOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-11T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JuradoAccesoQueryRepository repository;

    private JuradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JuradoAccesoQueryOutputAdapter(repository);
    }

    private UUID sembrarJurado(Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(JuradoJpaEntity.builder()
                .id(id)
                .identificador(id.toString().substring(0, 20))
                .nombre("Jurado")
                .email("jurado@uco.edu.co")
                .ocurridoEn(OCURRIDO_EN)
                .eliminadoEn(eliminadoEn)
                .build());
        entityManager.flush();
        entityManager.clear();
        return id;
    }

    @Test
    void debeRetornarTrue_cuandoElJuradoExiste() {
        // Arrange
        var jurado = sembrarJurado(null);

        // Act & Assert
        assertThat(adapter.existePorId(jurado)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoNoExiste() {
        // Act & Assert
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoElJuradoEstaRemovido() {
        // Arrange
        var jurado = sembrarJurado(OCURRIDO_EN);

        // Act & Assert
        assertThat(adapter.existePorId(jurado)).isTrue();
    }
}
