package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DataJpaTest
class ArtefactoCommandOutputAdapterTest {

    private static final Instant T0 = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private ArtefactoCommandRepository artefactoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ArtefactoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ArtefactoCommandOutputAdapter(artefactoCommandRepository, mock(AppLogger.class));
    }

    private static ArtefactoEntity entity(UUID id, UUID proyecto, String documento, int numeroVersion, String clave) {
        return new ArtefactoEntity(id, proyecto, UtilUUID.generarNuevoUUID(), numeroVersion, T0, clave,
                UtilUUID.generarNuevoUUID(), documento, UtilUUID.generarNuevoUUID(), "Repo", null, null);
    }

    private ArtefactoJpaEntity recargar(UUID id) {
        entityManager.flush();
        entityManager.clear();
        return entityManager.find(ArtefactoJpaEntity.class, id);
    }

    @Test
    void debeInsertarYLeerElArtefacto_cuandoNoExistiaFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var nuevo = entity(id, proyecto, "Informe", 1, "entregables/artefactos/x/v1");

        // Act
        adapter.insertar(nuevo);
        entityManager.flush();
        entityManager.clear();
        var leido = adapter.obtenerPorId(id);

        // Assert
        assertThat(leido).isPresent();
        assertThat(leido.get().proyectoGrado()).isEqualTo(proyecto);
        assertThat(leido.get().numeroVersion()).isEqualTo(1);
        assertThat(leido.get().nombreDocumento()).isEqualTo("Informe");
        assertThat(leido.get().claveObjeto()).isEqualTo("entregables/artefactos/x/v1");
        assertThat(leido.get().omitidoEn()).isNull();
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeAvanzarTodosLosCampos_cuandoLaVersionEsMayor() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(id, proyecto, "Informe", 1, "clave-v1"));
        entityManager.flush();
        entityManager.clear();
        var mayor = entity(id, proyecto, "Informe final", 2, "clave-v2");

        // Act
        var filas = adapter.avanzarVersion(mayor);
        var actual = recargar(id);

        // Assert
        assertThat(filas).isEqualTo(1);
        assertThat(actual.getNumeroVersion()).isEqualTo(2);
        assertThat(actual.getClaveObjeto()).isEqualTo("clave-v2");
        assertThat(actual.getVersionArtefacto()).isEqualTo(mayor.versionArtefacto());
        assertThat(actual.getNombreDocumento()).isEqualTo("Informe final");
        assertThat(actual.getVersionRepositorioArtefacto()).isEqualTo(mayor.versionRepositorioArtefacto());
    }

    @Test
    void noDebeCambiarNada_cuandoLaVersionEsMenorOIgual() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var original = entity(id, proyecto, "Informe", 3, "clave-v3");
        adapter.insertar(original);
        entityManager.flush();
        entityManager.clear();

        // Act
        var filasMenor = adapter.avanzarVersion(entity(id, proyecto, "Otro", 2, "clave-v2"));
        var filasIgual = adapter.avanzarVersion(entity(id, proyecto, "Otro", 3, "clave-igual"));
        var actual = recargar(id);

        // Assert
        assertThat(filasMenor).isZero();
        assertThat(filasIgual).isZero();
        assertThat(actual.getNumeroVersion()).isEqualTo(3);
        assertThat(actual.getClaveObjeto()).isEqualTo("clave-v3");
        assertThat(actual.getVersionArtefacto()).isEqualTo(original.versionArtefacto());
        assertThat(actual.getNombreDocumento()).isEqualTo("Informe");
    }

    @Test
    void debeMarcarLaOmision_cuandoElEventoEsMasNuevoQueElUltimoAplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(id, UtilUUID.generarNuevoUUID(), "Informe", 1, "clave"));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.marcarOmision(id, T0.plusSeconds(60), T0.plusSeconds(30));
        var actual = recargar(id);

        // Assert
        assertThat(actual.getOmitidoEn()).isEqualTo(T0.plusSeconds(60));
        assertThat(actual.getOmisionOcurridaEn()).isEqualTo(T0.plusSeconds(30));
    }

    @Test
    void noDebeCambiarLaOmision_cuandoElEventoEsMasViejoQueElAplicado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(id, UtilUUID.generarNuevoUUID(), "Informe", 1, "clave"));
        adapter.marcarOmision(id, T0.plusSeconds(60), T0.plusSeconds(30));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.limpiarOmision(id, T0.plusSeconds(10));
        adapter.marcarOmision(id, T0.plusSeconds(90), T0.plusSeconds(30));
        var actual = recargar(id);

        // Assert
        assertThat(actual.getOmitidoEn()).isEqualTo(T0.plusSeconds(60));
        assertThat(actual.getOmisionOcurridaEn()).isEqualTo(T0.plusSeconds(30));
    }

    @Test
    void debeLimpiarLaOmision_cuandoLaReactivacionEsMasNueva() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(id, UtilUUID.generarNuevoUUID(), "Informe", 1, "clave"));
        adapter.marcarOmision(id, T0.plusSeconds(60), T0.plusSeconds(30));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.limpiarOmision(id, T0.plusSeconds(120));
        var actual = recargar(id);

        // Assert
        assertThat(actual.getOmitidoEn()).isNull();
        assertThat(actual.getOmisionOcurridaEn()).isEqualTo(T0.plusSeconds(120));
    }

    @Test
    void debeDevolverSoloLosVigentesDelProyectoOrdenadosPorDocumento_cuandoHayOmitidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var omitido = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(UtilUUID.generarNuevoUUID(), proyecto, "Manual", 1, "c1"));
        adapter.insertar(entity(UtilUUID.generarNuevoUUID(), proyecto, "Anteproyecto", 1, "c2"));
        adapter.insertar(entity(omitido, proyecto, "Informe", 1, "c3"));
        adapter.insertar(entity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Ajeno", 1, "c4"));
        adapter.marcarOmision(omitido, T0, T0);
        entityManager.flush();
        entityManager.clear();

        // Act
        var vigentes = adapter.obtenerVigentesPorProyecto(proyecto);

        // Assert
        assertThat(vigentes).extracting(ArtefactoEntity::nombreDocumento).containsExactly("Anteproyecto", "Manual");
    }

    @Test
    void debeDevolverLasClavesDeTodasLasFilasDelProyecto_incluidasLasOmitidas() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var omitido = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(UtilUUID.generarNuevoUUID(), proyecto, "Manual", 1, "clave-a"));
        adapter.insertar(entity(omitido, proyecto, "Informe", 1, "clave-b"));
        adapter.insertar(entity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Ajeno", 1, "clave-x"));
        adapter.marcarOmision(omitido, T0, T0);
        entityManager.flush();
        entityManager.clear();

        // Act
        var claves = adapter.obtenerClavesPorProyecto(proyecto);

        // Assert
        assertThat(claves).containsExactlyInAnyOrder("clave-a", "clave-b");
    }

    @Test
    void debeRegistrarLapidaDeOmisionConValoresPorDefecto_cuandoNoHayFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrarLapidaOmitida(id, proyecto, T0.plusSeconds(60), T0.plusSeconds(30));
        entityManager.flush();
        entityManager.clear();
        var leida = adapter.obtenerPorId(id);
        var jpa = entityManager.find(ArtefactoJpaEntity.class, id);

        // Assert
        assertThat(jpa.getNumeroVersion()).isNull();
        assertThat(jpa.getClaveObjeto()).isNull();
        assertThat(leida).isPresent();
        assertThat(leida.get().proyectoGrado()).isEqualTo(proyecto);
        assertThat(leida.get().omitidoEn()).isEqualTo(T0.plusSeconds(60));
        assertThat(leida.get().omisionOcurridaEn()).isEqualTo(T0.plusSeconds(30));
        assertThat(UtilUUID.esPorDefecto(leida.get().versionArtefacto())).isTrue();
        assertThat(leida.get().numeroVersion()).isZero();
        assertThat(leida.get().fechaVersion()).isEqualTo(UtilFecha.VACIO);
        assertThat(leida.get().claveObjeto()).isEmpty();
        assertThat(leida.get().nombreDocumento()).isEmpty();
    }

    @Test
    void debeRegistrarLapidaDeReactivacionSinOmitidoEn_cuandoNoHayFila() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        adapter.registrarLapidaVigente(id, UtilUUID.generarNuevoUUID(), T0.plusSeconds(30));
        var actual = recargar(id);

        // Assert
        assertThat(actual.getOmitidoEn()).isNull();
        assertThat(actual.getOmisionOcurridaEn()).isEqualTo(T0.plusSeconds(30));
    }

    @Test
    void noDebeListarLapidasNiSusClaves_cuandoElProyectoTieneVersionesYLapidas() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(UtilUUID.generarNuevoUUID(), proyecto, "Manual", 1, "clave-a"));
        adapter.registrarLapidaVigente(UtilUUID.generarNuevoUUID(), proyecto, T0);
        adapter.registrarLapidaOmitida(UtilUUID.generarNuevoUUID(), proyecto, T0, T0);
        entityManager.flush();
        entityManager.clear();

        // Act
        var vigentes = adapter.obtenerVigentesPorProyecto(proyecto);
        var claves = adapter.obtenerClavesPorProyecto(proyecto);

        // Assert
        assertThat(vigentes).extracting(ArtefactoEntity::nombreDocumento).containsExactly("Manual");
        assertThat(claves).containsExactly("clave-a");
    }

    @Test
    void debeAvanzarSobreLapidaSinTocarLaOmision_cuandoElCierreLlegaDespues() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        adapter.registrarLapidaOmitida(id, proyecto, T0.plusSeconds(60), T0.plusSeconds(30));
        entityManager.flush();
        entityManager.clear();
        var cierre = entity(id, proyecto, "Informe", 1, "clave-v1");

        // Act
        var filas = adapter.avanzarVersion(cierre);
        var actual = recargar(id);

        // Assert
        assertThat(filas).isEqualTo(1);
        assertThat(actual.getNumeroVersion()).isEqualTo(1);
        assertThat(actual.getClaveObjeto()).isEqualTo("clave-v1");
        assertThat(actual.getNombreDocumento()).isEqualTo("Informe");
        assertThat(actual.getOmitidoEn()).isEqualTo(T0.plusSeconds(60));
        assertThat(actual.getOmisionOcurridaEn()).isEqualTo(T0.plusSeconds(30));
        assertThat(adapter.obtenerVigentesPorProyecto(proyecto)).isEmpty();
    }

    @Test
    void debeFallarPorLlavePrimaria_cuandoSeInsertaUnaFilaQueYaExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        adapter.insertar(entity(id, proyecto, "Informe", 1, "clave-v1"));
        entityManager.flush();

        // Act & Assert
        assertThatThrownBy(() -> adapter.insertar(entity(id, proyecto, "Informe", 2, "clave-v2")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
