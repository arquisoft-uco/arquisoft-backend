package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.biblioteca.infrastructure.bibliotecario.command.secondaryadapter.entity.BibliotecarioJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class BibliotecarioCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Autowired
    private BibliotecarioCommandRepository bibliotecarioCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private BibliotecarioCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BibliotecarioCommandOutputAdapter(bibliotecarioCommandRepository, logger);
    }

    private BibliotecarioJpaEntity eliminado(UUID id, String identificador) {
        return BibliotecarioJpaEntity.builder()
                .id(id).identificador(identificador).nombre("Ana Perez").email("ana@uco.edu.co")
                .ocurridoEn(OCURRIDO_EN).eliminadoEn(OCURRIDO_EN).build();
    }

    @Test
    void debeGuardarVigenteYLeerloDeVuelta_cuandoSePersiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = new BibliotecarioEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN,
                UtilFecha.VACIO);

        // Act
        adapter.guardar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(BibliotecarioJpaEntity.class, id).getEliminadoEn()).isNull();
        assertThat(adapter.obtenerPorId(id)).contains(entity);
        verify(logger).debug(BibliotecarioKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRefrescarDatosYLimpiarLaBajaSoloDeEseId_cuandoSeReactiva() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(eliminado(id, "20161020123"));
        entityManager.persistAndFlush(eliminado(otro, "20161020555"));
        var nuevoOcurridoEn = OCURRIDO_EN.plusSeconds(3600);
        var reactivado = new BibliotecarioEntity(id, "20161020999", "Ana Reactivada", "reactivada@uco.edu.co",
                nuevoOcurridoEn, UtilFecha.VACIO);

        // Act
        adapter.reactivar(reactivado);

        // Assert
        assertThat(adapter.obtenerPorId(id)).contains(reactivado);
        assertThat(adapter.obtenerPorId(otro)).hasValueSatisfying(intacto -> {
            assertThat(intacto.identificador()).isEqualTo("20161020555");
            assertThat(intacto.eliminadoEn()).isEqualTo(OCURRIDO_EN);
        });
        verify(logger).debug(BibliotecarioKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeActualizarEliminadoEnYOcurridoEnSoloDeEseId_cuandoSeEliminaLogica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(BibliotecarioJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez").email("ana@uco.edu.co")
                .ocurridoEn(OCURRIDO_EN).build());
        entityManager.persistAndFlush(BibliotecarioJpaEntity.builder()
                .id(otro).identificador("20161020555").nombre("Luis Gomez").email("luis@uco.edu.co")
                .ocurridoEn(OCURRIDO_EN).build());
        var nuevoOcurridoEn = OCURRIDO_EN.plusSeconds(3600);

        // Act
        adapter.eliminarLogica(id, nuevoOcurridoEn);

        // Assert
        assertThat(adapter.obtenerPorId(id)).hasValueSatisfying(removido -> {
            assertThat(removido.eliminadoEn()).isEqualTo(nuevoOcurridoEn);
            assertThat(removido.ocurridoEn()).isEqualTo(nuevoOcurridoEn);
            assertThat(removido.identificador()).isEqualTo("20161020123");
        });
        assertThat(adapter.obtenerPorId(otro)).hasValueSatisfying(intacto -> {
            assertThat(intacto.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
            assertThat(intacto.ocurridoEn()).isEqualTo(OCURRIDO_EN);
        });
        verify(logger).debug(BibliotecarioKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeRetornarLaBajaOVacio_cuandoSeConsultaPorId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persistAndFlush(eliminado(id, "20161020123"));

        // Act
        var existente = adapter.obtenerPorId(id);
        var inexistente = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existente).map(BibliotecarioEntity::eliminadoEn).contains(OCURRIDO_EN);
        assertThat(inexistente).isEmpty();
    }
}
