package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
import com.arquisoft.shared.util.UtilFecha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class JuradoCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-11T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JuradoCommandRepository repository;

    private final AppLogger logger = mock(AppLogger.class);

    private JuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JuradoCommandOutputAdapter(repository, logger);
    }

    @Test
    void debeDevolverSoloLosVigentesDelConjunto_cuandoHayRemovidosYDesconocidos() {
        // Arrange
        var vigente = sembrarJurado(null);
        var removido = sembrarJurado(Instant.now());
        var sinSembrar = UUID.randomUUID();
        var otroVigenteFueraDelConjunto = sembrarJurado(null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var vigentes = adapter.consultarVigentesPorIds(Set.of(vigente, removido, sinSembrar));

        // Assert
        assertThat(vigentes).containsExactly(vigente).doesNotContain(otroVigenteFueraDelConjunto);
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var id = UUID.randomUUID();
        var jurado = new JuradoEntity(id, "ID-1", "Jurado Uno", "j1@uco.edu.co", OCURRIDO_EN, UtilFecha.VACIO);

        // Act
        adapter.guardar(jurado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(JuradoJpaEntity.class, id).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorId(id)).contains(jurado);
        verify(logger).debug(JuradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRefrescarDatosYLimpiarLaBajaSoloDeEseId_cuandoSeReactiva() {
        // Arrange
        var id = sembrarJurado(Instant.parse("2026-09-24T10:00:00Z"));
        var otro = sembrarJurado(Instant.parse("2026-09-24T10:00:00Z"));
        entityManager.flush();
        entityManager.clear();
        var reactivado = new JuradoEntity(id, "ID-N", "Nombre Nuevo", "nuevo@uco.edu.co", OCURRIDO_EN,
                UtilFecha.VACIO);

        // Act
        adapter.reactivar(reactivado);

        // Assert
        assertThat(adapter.obtenerPorId(id)).contains(reactivado);
        assertThat(adapter.obtenerPorId(otro)).map(JuradoEntity::eliminadoEn)
                .contains(Instant.parse("2026-09-24T10:00:00Z"));
        verify(logger).debug(JuradoKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeFijarEliminadoEnYOcurridoEnSoloDeEseJurado_cuandoSeEliminaLogica() {
        // Arrange
        var id = sembrarJurado(null);
        var otro = sembrarJurado(null);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.eliminarLogica(id, OCURRIDO_EN);

        // Assert
        var removido = entityManager.find(JuradoJpaEntity.class, id);
        assertThat(removido.getEliminadoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(removido.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(entityManager.find(JuradoJpaEntity.class, otro).getEliminadoEn()).isNull();
        assertThat(adapter.consultarVigentesPorIds(Set.of(id, otro))).containsExactly(otro);
        verify(logger).debug(JuradoKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Act
        var jurado = adapter.obtenerPorId(UUID.randomUUID());

        // Assert
        assertThat(jurado).isEmpty();
    }

    private UUID sembrarJurado(Instant eliminadoEn) {
        var id = UUID.randomUUID();
        entityManager.persist(JuradoJpaEntity.builder()
                .id(id)
                .identificador("ID-" + id.toString().substring(0, 8))
                .nombre("Jurado")
                .email("jurado@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z"))
                .eliminadoEn(eliminadoEn)
                .build());
        return id;
    }
}
