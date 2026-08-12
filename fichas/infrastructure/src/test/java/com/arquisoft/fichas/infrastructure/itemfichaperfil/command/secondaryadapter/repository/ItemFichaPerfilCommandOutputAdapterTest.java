package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper.ItemFichaPerfilMapper;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import com.arquisoft.fichas.application.tipoitem.command.secondaryport.entity.TipoItemEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class ItemFichaPerfilCommandOutputAdapterTest {

    @Autowired
    private ItemFichaPerfilCommandRepository repository;

    @Autowired
    private EntityManager entityManager;

    private ItemFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ItemFichaPerfilCommandOutputAdapter(repository);

        // Seed: insertar tipo_item en H2 para que la referencia por id funcione
        entityManager.createNativeQuery(
                "INSERT INTO tipo_item (id, nombre, descripcion) VALUES (?, ?, ?)"
        )
                .setParameter(1, "OBJETIVO_GENERAL")
                .setParameter(2, "Objetivo General")
                .setParameter(3, "Descripción de prueba")
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeGuardar_cuandoItemEsValido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Este es un objetivo general de prueba"
        );

        // Act
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();

        // Assert
        ItemFichaPerfilEntity savedEntity = repository.findById(aggregate.getId()).orElse(null);
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isEqualTo(aggregate.getId());
        assertThat(savedEntity.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(savedEntity.getTipoItem().getId()).isEqualTo("OBJETIVO_GENERAL");
        assertThat(savedEntity.getContenido()).isEqualTo("Este es un objetivo general de prueba");
    }

    @Test
    void debeUsarGetReference_cuandoGuarda() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido de prueba"
        );

        // Act
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();

        // Assert — getReference crea un proxy sin SELECT
        // Verificamos que el entity guardado tiene el tipo correcto sin haber cargado toda la entidad
        ItemFichaPerfilEntity saved = repository.findById(aggregate.getId()).orElseThrow();
        assertThat(saved.getTipoItem()).isNotNull();
        assertThat(saved.getTipoItem().getId()).isEqualTo("OBJETIVO_GENERAL");
    }

    @Test
    void debeRetornarTrue_cuandoParExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";

        TipoItemEntity tipoItemRef = entityManager.getReference(TipoItemEntity.class, tipoItem);
        ItemFichaPerfilEntity entity = ItemFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItemRef)
                .contenido("Contenido existente")
                .build();
        repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = adapter.existePorFichaYTipoItem(fichaPerfilId, tipoItem);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoParNoExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";

        // Act
        boolean existe = adapter.existePorFichaYTipoItem(fichaPerfilId, tipoItem);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoItemExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        TipoItemEntity tipoItemRef = entityManager.getReference(TipoItemEntity.class, "OBJETIVO_GENERAL");
        ItemFichaPerfilEntity entity = ItemFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItemRef)
                .contenido("Contenido existente")
                .build();
        ItemFichaPerfilEntity saved = repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = adapter.existePorId(saved.getId());

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoItemNoExiste() {
        // Arrange
        UUID itemIdInexistente = UUID.randomUUID();

        // Act
        boolean existe = adapter.existePorId(itemIdInexistente);

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void debeActualizarSoloElContenido_cuandoModificarContenido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido inicial"
        );
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarContenido(aggregate.getId(), "Contenido modificado");
        entityManager.flush();
        entityManager.clear();

        // Assert
        ItemFichaPerfilEntity savedEntity = repository.findById(aggregate.getId()).orElseThrow();
        assertThat(savedEntity.getContenido()).isEqualTo("Contenido modificado");
        assertThat(savedEntity.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(savedEntity.getTipoItem().getId()).isEqualTo("OBJETIVO_GENERAL");
    }

    @Test
    void debeRetornarFichaPerfilId_cuandoItemExiste() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                "OBJETIVO_GENERAL",
                "Contenido inicial"
        );
        adapter.registrarItem(ItemFichaPerfilMapper.toEntity(aggregate));
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<UUID> resultado = adapter.obtenerFichaPerfilId(aggregate.getId());

        // Assert
        assertThat(resultado).contains(fichaPerfilId);
    }

    @Test
    void debeRetornarVacio_cuandoItemNoExisteParaFichaPerfilId() {
        // Act
        Optional<UUID> resultado = adapter.obtenerFichaPerfilId(UUID.randomUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeEliminar_cuandoIdValido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        TipoItemEntity tipoItemRef = entityManager.getReference(TipoItemEntity.class, "OBJETIVO_GENERAL");
        ItemFichaPerfilEntity entity = ItemFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .tipoItem(tipoItemRef)
                .contenido("Contenido a eliminar")
                .build();
        ItemFichaPerfilEntity saved = repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.removerItem(saved.getId());
        entityManager.flush();

        // Assert
        assertThat(repository.existsById(saved.getId())).isFalse();
    }
}
