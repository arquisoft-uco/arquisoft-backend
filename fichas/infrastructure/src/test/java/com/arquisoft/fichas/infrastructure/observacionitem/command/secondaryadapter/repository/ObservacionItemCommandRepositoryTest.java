package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ObservacionItemCommandRepositoryTest {

    @Autowired
    private ObservacionItemCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    private record FichaSembrada(UUID ficha, UUID asesor, UUID item) {}

    private void sembrarEstadoObservacionRevision(String id) {
        entityManager.createNativeQuery(
                "INSERT INTO estado_observacion_revision (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, "Estado de prueba " + id)
                .executeUpdate();
    }

    private void sembrarEstadoRevision(String id) {
        entityManager.createNativeQuery(
                "INSERT INTO estado_revision (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, "Estado de prueba " + id)
                .executeUpdate();
    }

    private UUID sembrarRevisionItem() {
        var revisionItemId = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)")
                .setParameter(1, revisionItemId)
                .setParameter(2, UUID.randomUUID())
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now())
                .executeUpdate();
        return revisionItemId;
    }

    private void sembrarObservacionItem(UUID revisionItemId, String observacion) {
        entityManager.createNativeQuery(
                "INSERT INTO observacion_item (id, revision_item_id, observacion, "
                        + "estado_observacion_revision_id) VALUES (?, ?, ?, ?)")
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, revisionItemId)
                .setParameter(3, observacion)
                .setParameter(4, "PENDIENTE")
                .executeUpdate();
    }

    private FichaSembrada sembrarFichaConItem() {
        var asesor = testEntityManager.persistAndFlush(AsesorFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).identificador("ASE-001").nombre("Asesor de prueba")
                .email("asesor@uco.edu.co").ocurridoEn(Instant.now()).build());
        var ficha = testEntityManager.persistAndFlush(FichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).tituloProyecto("Ficha de prueba").asesorFicha(asesor).build());
        var tipoItem = testEntityManager.find(TipoItemJpaEntity.class, "TIPO_PRUEBA");
        if (tipoItem == null) {
            tipoItem = testEntityManager.persistAndFlush(TipoItemJpaEntity.builder()
                    .id("TIPO_PRUEBA").nombre("Tipo").descripcion("Tipo de prueba").build());
        }
        var item = testEntityManager.persistAndFlush(ItemFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).fichaPerfilId(ficha.getId())
                .tipoItem(tipoItem).contenido("Contenido").build());
        return new FichaSembrada(ficha.getId(), asesor.getId(), item.getId());
    }

    private UUID sembrarRevisionDelItem(UUID item, String estado) {
        var estadoRevision = testEntityManager.find(EstadoRevisionJpaEntity.class, estado);
        if (estadoRevision == null) {
            estadoRevision = testEntityManager.persistAndFlush(EstadoRevisionJpaEntity.builder()
                    .id(estado).nombre(estado).descripcion("Estado de prueba " + estado).build());
        }
        var revision = testEntityManager.persistAndFlush(RevisionItemJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).itemId(item).estadoRevision(estadoRevision)
                .fechaCreacion(Instant.now()).build());
        return revision.getId();
    }

    private UUID sembrarObservacionDeLaRevision(UUID revision, String texto, String estado) {
        var estadoObservacion = testEntityManager.find(EstadoObservacionRevisionJpaEntity.class, estado);
        if (estadoObservacion == null) {
            estadoObservacion = testEntityManager.persistAndFlush(EstadoObservacionRevisionJpaEntity.builder()
                    .id(estado).nombre(estado).descripcion("Estado de prueba " + estado).build());
        }
        var observacion = testEntityManager.persistAndFlush(ObservacionItemJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).revisionItemId(revision).observacion(texto)
                .estadoObservacionRevision(estadoObservacion).build());
        return observacion.getId();
    }

    @Test
    void debeContarUno_cuandoElTextoCoincideExactamenteEnLaMismaRevision() {
        // Arrange
        sembrarEstadoObservacionRevision("PENDIENTE");
        sembrarEstadoRevision("NUEVA");
        var revisionItemId = sembrarRevisionItem();
        sembrarObservacionItem(revisionItemId, "Observación válida");
        entityManager.flush();
        entityManager.clear();

        // Act
        var count = repository.countByRevisionItemIdAndObservacion(revisionItemId, "Observación válida");

        // Assert
        assertThat(count).isEqualTo(1);
    }

    @Test
    void debeRetornarCero_cuandoElTextoEsDistinto() {
        // Arrange
        sembrarEstadoObservacionRevision("PENDIENTE");
        sembrarEstadoRevision("NUEVA");
        var revisionItemId = sembrarRevisionItem();
        sembrarObservacionItem(revisionItemId, "Observación válida");
        entityManager.flush();
        entityManager.clear();

        // Act
        var count = repository.countByRevisionItemIdAndObservacion(revisionItemId, "Otro texto");

        // Assert
        assertThat(count).isZero();
    }

    @Test
    void debeRetornarCero_cuandoElTextoCoincideEnOtraRevision() {
        // Arrange
        sembrarEstadoObservacionRevision("PENDIENTE");
        sembrarEstadoRevision("NUEVA");
        var revisionItemId = sembrarRevisionItem();
        var otraRevisionItemId = sembrarRevisionItem();
        sembrarObservacionItem(revisionItemId, "Observación válida");
        entityManager.flush();
        entityManager.clear();

        // Act
        var count = repository.countByRevisionItemIdAndObservacion(otraRevisionItemId, "Observación válida");

        // Assert
        assertThat(count).isZero();
    }

    @Test
    void debeDevolverElContextoCompleto_cuandoLaObservacionExiste() {
        // Arrange
        var ficha = sembrarFichaConItem();
        var revision = sembrarRevisionDelItem(ficha.item(), "EN_PROGRESO");
        var observacion = sembrarObservacionDeLaRevision(revision, "Observación válida", "PENDIENTE");
        testEntityManager.clear();

        // Act
        var resultado = repository.obtenerContexto(observacion);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().revisionItem()).isEqualTo(revision);
        assertThat(resultado.get().estadoRevision()).isEqualTo("EN_PROGRESO");
        assertThat(resultado.get().fichaPerfil()).isEqualTo(ficha.ficha());
        assertThat(resultado.get().asesorFicha()).isEqualTo(ficha.asesor());
    }

    @Test
    void debeDevolverVacio_cuandoLaObservacionNoExiste() {
        // Act
        var resultado = repository.obtenerContexto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeExcluirLaPropiaObservacion_cuandoElTextoNuevoEsElActual() {
        // Arrange
        var ficha = sembrarFichaConItem();
        var revision = sembrarRevisionDelItem(ficha.item(), "NUEVA");
        var observacion = sembrarObservacionDeLaRevision(revision, "Observación válida", "PENDIENTE");
        testEntityManager.clear();

        // Act
        var otrasIguales = repository.contarOtrasIgualesEnRevision(observacion, "Observación válida");

        // Assert
        assertThat(otrasIguales).isZero();
    }

    @Test
    void debeContarLaHermana_cuandoOtraObservacionDeLaRevisionTieneElMismoTexto() {
        // Arrange
        var ficha = sembrarFichaConItem();
        var revision = sembrarRevisionDelItem(ficha.item(), "NUEVA");
        var observacion = sembrarObservacionDeLaRevision(revision, "Texto original", "PENDIENTE");
        sembrarObservacionDeLaRevision(revision, "Texto nuevo", "PENDIENTE");
        sembrarObservacionDeLaRevision(revision, "Texto distinto", "PENDIENTE");
        testEntityManager.clear();

        // Act
        var otrasIguales = repository.contarOtrasIgualesEnRevision(observacion, "Texto nuevo");

        // Assert
        assertThat(otrasIguales).isEqualTo(1L);
    }

    @Test
    void debeRetornarCero_cuandoElMismoTextoEstaEnObservacionDeOtraRevision() {
        // Arrange
        var ficha = sembrarFichaConItem();
        var revision = sembrarRevisionDelItem(ficha.item(), "NUEVA");
        var otraRevision = sembrarRevisionDelItem(ficha.item(), "NUEVA");
        var observacion = sembrarObservacionDeLaRevision(revision, "Texto original", "PENDIENTE");
        sembrarObservacionDeLaRevision(otraRevision, "Texto nuevo", "PENDIENTE");
        testEntityManager.clear();

        // Act
        var otrasIguales = repository.contarOtrasIgualesEnRevision(observacion, "Texto nuevo");

        // Assert
        assertThat(otrasIguales).isZero();
    }

    @Test
    void debePersistirSoloElTexto_cuandoActualizaLaObservacion() {
        // Arrange
        var ficha = sembrarFichaConItem();
        var revision = sembrarRevisionDelItem(ficha.item(), "EN_PROGRESO");
        var observacion = sembrarObservacionDeLaRevision(revision, "Texto original", "EN_PROGRESO");
        var hermana = sembrarObservacionDeLaRevision(revision, "Texto de la hermana", "PENDIENTE");
        testEntityManager.clear();

        // Act
        var filas = repository.actualizarObservacion(observacion, "Texto nuevo");

        // Assert
        var persistida = testEntityManager.find(ObservacionItemJpaEntity.class, observacion);
        var intacta = testEntityManager.find(ObservacionItemJpaEntity.class, hermana);
        assertThat(filas).isEqualTo(1);
        assertThat(persistida.getObservacion()).isEqualTo("Texto nuevo");
        assertThat(persistida.getRevisionItemId()).isEqualTo(revision);
        assertThat(persistida.getEstadoObservacionRevision().getId()).isEqualTo("EN_PROGRESO");
        assertThat(intacta.getObservacion()).isEqualTo("Texto de la hermana");
    }

    @Test
    void debeEliminarSoloLaObservacionIndicada_cuandoRemuevePorId() {
        // Arrange
        var ficha = sembrarFichaConItem();
        var revision = sembrarRevisionDelItem(ficha.item(), "EN_PROGRESO");
        var observacion = sembrarObservacionDeLaRevision(revision, "Texto a remover", "EN_PROGRESO");
        var hermana = sembrarObservacionDeLaRevision(revision, "Texto de la hermana", "PENDIENTE");
        testEntityManager.clear();

        // Act
        var filas = repository.removerPorId(observacion);

        // Assert — la hermana y la revisión (con su estado) sobreviven
        assertThat(filas).isEqualTo(1);
        assertThat(testEntityManager.find(ObservacionItemJpaEntity.class, observacion)).isNull();
        assertThat(testEntityManager.find(ObservacionItemJpaEntity.class, hermana)).isNotNull();
        var revisionPersistida = testEntityManager.find(RevisionItemJpaEntity.class, revision);
        assertThat(revisionPersistida).isNotNull();
        assertThat(revisionPersistida.getEstadoRevision().getId()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void debeRetornarCeroFilas_cuandoLaObservacionNoExiste() {
        // Act
        var filas = repository.removerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(filas).isZero();
    }
}
