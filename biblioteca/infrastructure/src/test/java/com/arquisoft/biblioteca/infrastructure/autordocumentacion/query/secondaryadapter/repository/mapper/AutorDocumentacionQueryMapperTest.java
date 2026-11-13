package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository.AutorDocumentacionJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutorDocumentacionQueryMapperTest {

    @Test
    void debeTomarElIdDelAutorYNoElDelVinculo_cuandoMapeaALectura() {
        // Arrange
        var vinculo = UtilUUID.generarNuevoUUID();
        var autor = UtilUUID.generarNuevoUUID();
        var entity = AutorDocumentacionJpaQueryEntity.builder()
                .id(vinculo).documentacionId(UtilUUID.generarNuevoUUID()).autorId(autor)
                .identificador("20161020123").nombre("Ana Perez").email("ana@uco.edu.co").build();

        // Act
        var readModel = AutorDocumentacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(autor);
        assertThat(readModel.identificador()).isEqualTo("20161020123");
        assertThat(readModel.nombre()).isEqualTo("Ana Perez");
        assertThat(readModel.email()).isEqualTo("ana@uco.edu.co");
    }
}
