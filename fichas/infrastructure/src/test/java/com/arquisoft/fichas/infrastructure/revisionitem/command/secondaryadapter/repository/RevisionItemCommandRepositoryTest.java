package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class RevisionItemCommandRepositoryTest {

    @Autowired
    private RevisionItemCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    private void sembrarEstadoRevision(String id) {
        entityManager.createNativeQuery(
                "INSERT INTO estado_revision (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, id)
                .setParameter(3, "Estado de prueba " + id)
                .executeUpdate();
    }

    @Test
    void debeContarRevisiones_cuandoItemTieneRevisiones() {
        // Arrange
        UUID itemId = UUID.randomUUID();

        sembrarEstadoRevision("NUEVA");
        sembrarEstadoRevision("VISUALIZADA");

        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)"
        )
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemId)
                .setParameter(3, "NUEVA")
                .setParameter(4, Instant.now())
                .executeUpdate();

        entityManager.createNativeQuery(
                "INSERT INTO revision_item (id, item_id, estado_revision_id, fecha_creacion) VALUES (?, ?, ?, ?)"
        )
                .setParameter(1, UUID.randomUUID())
                .setParameter(2, itemId)
                .setParameter(3, "VISUALIZADA")
                .setParameter(4, Instant.now().plusSeconds(60))
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();

        // Act
        long count = repository.countByItemId(itemId);

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    void debeRetornarCero_cuandoItemSinRevisiones() {
        // Arrange
        UUID itemIdSinRevisiones = UUID.randomUUID();

        // Act
        long count = repository.countByItemId(itemIdSinRevisiones);

        // Assert
        assertThat(count).isZero();
    }

    private RevisionItemJpaEntity sembrarRevision(UUID fichaPerfil, UUID estudianteVinculado, String estado) {
        var estadoRevision = testEntityManager.persistAndFlush(EstadoRevisionJpaEntity.builder()
                .id(estado).nombre(estado).descripcion("Estado de prueba " + estado).build());
        var tipoItem = testEntityManager.persistAndFlush(TipoItemJpaEntity.builder()
                .id("TIPO_PRUEBA").nombre("Tipo").descripcion("Tipo de prueba").build());
        var item = testEntityManager.persistAndFlush(ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaPerfil).tipoItem(tipoItem).contenido("Contenido").build());
        testEntityManager.persistAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaPerfil).estudianteId(estudianteVinculado).build());
        var revision = testEntityManager.persistAndFlush(RevisionItemJpaEntity.builder()
                .id(UUID.randomUUID()).itemId(item.getId()).estadoRevision(estadoRevision)
                .fechaCreacion(Instant.now()).build());
        testEntityManager.clear();
        return revision;
    }

    @Test
    void debeMarcarPropietarioYDevolverElEstado_cuandoElEstudianteEstaVinculadoALaFicha() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var revision = sembrarRevision(fichaPerfil, estudiante, "NUEVA");

        // Act
        var resultado = repository.obtenerPertenencia(revision.getId(), estudiante);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().fichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(resultado.get().esPropietario()).isTrue();
        assertThat(resultado.get().estadoRevision()).isEqualTo("NUEVA");
    }

    @Test
    void debeMarcarNoPropietario_cuandoElEstudianteNoEstaVinculadoALaFicha() {
        // Arrange
        var revision = sembrarRevision(UUID.randomUUID(), UUID.randomUUID(), "EN_PROGRESO");

        // Act
        var resultado = repository.obtenerPertenencia(revision.getId(), UUID.randomUUID());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().esPropietario()).isFalse();
        assertThat(resultado.get().estadoRevision()).isEqualTo("EN_PROGRESO");
    }

    @Test
    void debeRetornarVacio_cuandoLaRevisionNoExiste() {
        // Act
        var resultado = repository.obtenerPertenencia(UUID.randomUUID(), UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeActualizarUnaFilaYPersistir_cuandoElEstadoActualCoincide() {
        // Arrange
        var revision = sembrarRevision(UUID.randomUUID(), UUID.randomUUID(), "NUEVA");
        testEntityManager.persistAndFlush(EstadoRevisionJpaEntity.builder()
                .id("VISUALIZADA").nombre("VISUALIZADA").descripcion("Estado de prueba").build());

        // Act
        var filas = repository.actualizarEstado(revision.getId(), "NUEVA", "VISUALIZADA");

        // Assert
        var persistida = testEntityManager.find(RevisionItemJpaEntity.class, revision.getId());
        assertThat(filas).isEqualTo(1);
        assertThat(persistida.getEstadoRevision().getId()).isEqualTo("VISUALIZADA");
    }

    @Test
    void debeAfectarCeroFilasYNoCambiarNada_cuandoElEstadoActualDifiere() {
        // Arrange
        var revision = sembrarRevision(UUID.randomUUID(), UUID.randomUUID(), "EN_PROGRESO");
        testEntityManager.persistAndFlush(EstadoRevisionJpaEntity.builder()
                .id("VISUALIZADA").nombre("VISUALIZADA").descripcion("Estado de prueba").build());

        // Act
        var filas = repository.actualizarEstado(revision.getId(), "NUEVA", "VISUALIZADA");

        // Assert
        var persistida = testEntityManager.find(RevisionItemJpaEntity.class, revision.getId());
        assertThat(filas).isZero();
        assertThat(persistida.getEstadoRevision().getId()).isEqualTo("EN_PROGRESO");
    }

    private ItemFichaPerfilJpaEntity sembrarItemDeFicha(UUID fichaPerfil, UUID asesor) {
        var asesorFicha = testEntityManager.persistAndFlush(AsesorFichaJpaEntity.builder()
                .id(asesor).identificador("ASE-" + asesor.toString().substring(0, 8)).nombre("Asesor").email("asesor@uco.edu.co")
                .ocurridoEn(Instant.now()).build());
        testEntityManager.persistAndFlush(FichaPerfilJpaEntity.builder()
                .id(fichaPerfil).tituloProyecto("Proyecto").asesorFicha(asesorFicha).build());
        var tipoItem = testEntityManager.persistAndFlush(TipoItemJpaEntity.builder()
                .id("TIPO_PRUEBA").nombre("Tipo").descripcion("Tipo de prueba").build());
        return testEntityManager.persistAndFlush(ItemFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaPerfil).tipoItem(tipoItem).contenido("Contenido").build());
    }

    private RevisionItemJpaEntity sembrarRevisionEnItem(UUID item, String estado) {
        var estadoRevision = Optional.ofNullable(testEntityManager.find(EstadoRevisionJpaEntity.class, estado))
                .orElseGet(() -> testEntityManager.persistAndFlush(EstadoRevisionJpaEntity.builder()
                        .id(estado).nombre(estado).descripcion("Estado de prueba " + estado).build()));
        return testEntityManager.persistAndFlush(RevisionItemJpaEntity.builder()
                .id(UUID.randomUUID()).itemId(item).estadoRevision(estadoRevision)
                .fechaCreacion(Instant.now()).build());
    }

    @Test
    void debeObtenerFichaAsesorYEstado_cuandoLaRevisionExiste() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var item = sembrarItemDeFicha(fichaPerfil, asesor);
        var revision = sembrarRevisionEnItem(item.getId(), "CORRECCION_DISPONIBLE");
        testEntityManager.clear();

        // Act
        var resultado = repository.obtenerAsesoria(revision.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().fichaPerfilId()).isEqualTo(fichaPerfil);
        assertThat(resultado.get().asesorFichaId()).isEqualTo(asesor);
        assertThat(resultado.get().estadoRevision()).isEqualTo("CORRECCION_DISPONIBLE");
    }

    @Test
    void debeRetornarVacioLaAsesoria_cuandoLaRevisionNoExiste() {
        // Act
        var resultado = repository.obtenerAsesoria(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRemoverSoloLaRevisionIndicada_cuandoExisteYElItemTieneOtras() {
        // Arrange
        var item = sembrarItemDeFicha(UUID.randomUUID(), UUID.randomUUID());
        var aRemover = sembrarRevisionEnItem(item.getId(), "NUEVA");
        var aConservar = sembrarRevisionEnItem(item.getId(), "VISUALIZADA");
        testEntityManager.clear();

        // Act
        var filas = repository.removerPorId(aRemover.getId());

        // Assert
        assertThat(filas).isEqualTo(1);
        assertThat(testEntityManager.find(RevisionItemJpaEntity.class, aRemover.getId())).isNull();
        assertThat(testEntityManager.find(RevisionItemJpaEntity.class, aConservar.getId())).isNotNull();
        assertThat(repository.countByItemId(item.getId())).isEqualTo(1);
    }

    @Test
    void debeAfectarCeroFilas_cuandoLaRevisionARemoverNoExiste() {
        // Act
        var filas = repository.removerPorId(UUID.randomUUID());

        // Assert
        assertThat(filas).isZero();
    }
}
