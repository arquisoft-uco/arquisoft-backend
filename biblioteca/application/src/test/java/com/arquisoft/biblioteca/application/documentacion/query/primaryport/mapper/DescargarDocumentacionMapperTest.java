package com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.DescargarDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DescargarDocumentacionMapperTest {

    @Test
    void debeMarcarComoBibliotecario_cuandoLosRolesLoIncluyen() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var query = DescargarDocumentacionQuery.crear(documentacion,
                List.of(RolConsultaDocumentacion.ESTUDIANTE, RolConsultaDocumentacion.BIBLIOTECARIO));

        // Act
        var criteria = DescargarDocumentacionMapper.toCriteria(query);

        // Assert
        assertThat(criteria.documentacion()).isEqualTo(documentacion);
        assertThat(criteria.comoBibliotecario()).isTrue();
    }

    @Test
    void debeNoMarcarComoBibliotecario_cuandoLosRolesNoLoIncluyen() {
        // Arrange
        var conOtroRol = DescargarDocumentacionQuery.crear(UtilUUID.generarNuevoUUID(),
                List.of(RolConsultaDocumentacion.ASESOR));
        var sinRoles = DescargarDocumentacionQuery.crear(UtilUUID.generarNuevoUUID(), List.of());

        // Act & Assert
        assertThat(DescargarDocumentacionMapper.toCriteria(conOtroRol).comoBibliotecario()).isFalse();
        assertThat(DescargarDocumentacionMapper.toCriteria(sinRoles).comoBibliotecario()).isFalse();
    }
}
