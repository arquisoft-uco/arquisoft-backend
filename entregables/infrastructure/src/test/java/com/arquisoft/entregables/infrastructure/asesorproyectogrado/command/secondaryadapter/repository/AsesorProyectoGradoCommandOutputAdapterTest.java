package com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
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

@DataJpaTest
class AsesorProyectoGradoCommandOutputAdapterTest {

    private static final Instant T0 = Instant.parse("2026-11-01T10:00:00Z");
    private static final Instant T1 = Instant.parse("2026-11-02T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-11-03T10:00:00Z");

    @Autowired
    private AsesorProyectoGradoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private AsesorProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorProyectoGradoCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    private static AsesorProyectoGradoEntity vinculo(UUID asesor, UUID proyecto, Instant ocurridoEn,
                                                     Instant finalizadoEn) {
        return new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, ocurridoEn, finalizadoEn);
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeGuardarYLeerPorElPar_cuandoNoExistiaElVinculo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var buscado = vinculo(asesor, proyecto, T0, UtilFecha.VACIO);
        adapter.guardar(buscado);
        adapter.guardar(vinculo(asesor, UtilUUID.generarNuevoUUID(), T0, UtilFecha.VACIO));
        confirmar();

        // Act
        var encontrado = adapter.obtenerPorAsesorYProyectoGrado(asesor, proyecto);
        var ausente = adapter.obtenerPorAsesorYProyectoGrado(UtilUUID.generarNuevoUUID(), proyecto);

        // Assert
        assertThat(encontrado).contains(buscado);
        assertThat(ausente).isEmpty();
        assertThat(entityManager.find(AsesorProyectoGradoJpaEntity.class, buscado.id()).getFinalizadoEn()).isNull();
    }

    @Test
    void debeActualizarLaMismaFila_cuandoSeGuardaElVinculoFinalizado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var original = vinculo(asesor, proyecto, T0, UtilFecha.VACIO);
        adapter.guardar(original);
        confirmar();

        // Act
        adapter.guardar(new AsesorProyectoGradoEntity(original.id(), asesor, proyecto, T1, T1));
        confirmar();
        var leido = adapter.obtenerPorAsesorYProyectoGrado(asesor, proyecto);

        // Assert
        assertThat(leido).hasValueSatisfying(actual -> {
            assertThat(actual.id()).isEqualTo(original.id());
            assertThat(actual.ocurridoEn()).isEqualTo(T1);
            assertThat(actual.finalizadoEn()).isEqualTo(T1);
        });
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void debeRetornarElAsesorDelVinculoMasRecienteSinFinalizar_cuandoElProyectoCambioDeAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var nuevo = UtilUUID.generarNuevoUUID();
        adapter.guardar(vinculo(anterior, proyecto, T0, T1));
        adapter.guardar(vinculo(nuevo, proyecto, T1, UtilFecha.VACIO));
        adapter.guardar(vinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), T2, UtilFecha.VACIO));
        confirmar();

        // Act
        var vigente = adapter.obtenerAsesorVigentePorProyectoGrado(proyecto);

        // Assert
        assertThat(vigente).contains(nuevo);
    }

    @Test
    void debeRetornarVacio_cuandoElVinculoMasRecienteEstaFinalizadoOElProyectoNoTieneVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var anterior = UtilUUID.generarNuevoUUID();
        var ultimo = UtilUUID.generarNuevoUUID();
        adapter.guardar(vinculo(anterior, proyecto, T0, UtilFecha.VACIO));
        adapter.guardar(vinculo(ultimo, proyecto, T1, T2));
        confirmar();

        // Act & Assert
        assertThat(adapter.obtenerAsesorVigentePorProyectoGrado(proyecto)).isEmpty();
        assertThat(adapter.obtenerAsesorVigentePorProyectoGrado(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeRetornarElAsesorVigenteDelProyectoDelEntregable_cuandoLoGeneroOtroAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var generador = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, generador));
        adapter.guardar(vinculo(generador, proyecto, T0, T1));
        adapter.guardar(vinculo(vigente, proyecto, T1, UtilFecha.VACIO));
        confirmar();

        // Act
        var resultado = adapter.obtenerAsesorVigentePorEntregable(entregable);

        // Assert
        assertThat(resultado).contains(vigente);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoExisteOSuProyectoNoTieneAsesorVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EntregableProyectoGradoJpaEntity(entregable, proyecto, 1, asesor));
        adapter.guardar(vinculo(asesor, proyecto, T0, T1));
        confirmar();

        // Act & Assert
        assertThat(adapter.obtenerAsesorVigentePorEntregable(entregable)).isEmpty();
        assertThat(adapter.obtenerAsesorVigentePorEntregable(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
