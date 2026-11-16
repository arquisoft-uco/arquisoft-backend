package com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.entity.AutorDocumentacionEntity;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.entity.AutorDocumentacionJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorDocumentacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class AutorDocumentacionCommandOutputAdapterTest {

    @Autowired
    private AutorDocumentacionCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private AutorDocumentacionCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AutorDocumentacionCommandOutputAdapter(repository, logger);
    }

    @Test
    void debePersistirUnaFilaPorAutor_cuandoSeRegistranTodos() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var autores = List.of(
                new AutorDocumentacionEntity(UtilUUID.generarNuevoUUID(), primero, documentacion),
                new AutorDocumentacionEntity(UtilUUID.generarNuevoUUID(), segundo, documentacion));

        // Act
        adapter.registrarTodos(autores);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(repository.findAll())
                .extracting(AutorDocumentacionJpaEntity::getAutorId, AutorDocumentacionJpaEntity::getDocumentacionId)
                .containsExactlyInAnyOrder(
                        tuple(primero, documentacion),
                        tuple(segundo, documentacion));
        verify(logger).debug(AutorDocumentacionKey.LOG_GUARDADOS, 2);
    }
}
