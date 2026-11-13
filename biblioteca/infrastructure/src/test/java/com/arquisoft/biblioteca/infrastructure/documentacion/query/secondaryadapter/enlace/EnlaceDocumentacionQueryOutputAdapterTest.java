package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.enlace;

import com.arquisoft.biblioteca.infrastructure.config.BibliotecaEnlacesProperties;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnlaceDocumentacionQueryOutputAdapterTest {

    private static EnlaceDocumentacionQueryOutputAdapter adaptador(String urlBase) {
        var propiedades = new BibliotecaEnlacesProperties();
        propiedades.setUrlBase(urlBase);
        propiedades.setRutaDocumentacion("/doc/{documentacionId}");
        return new EnlaceDocumentacionQueryOutputAdapter(propiedades);
    }

    @Test
    void debeComponerEnlace_conElMarcadorReemplazado() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var enlace = adaptador("https://front.test").generar(documentacion);

        // Assert
        assertThat(enlace).isEqualTo("https://front.test/doc/" + documentacion);
    }

    @Test
    void debeNoDuplicarBarra_cuandoUrlBaseTerminaEnBarra() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var enlace = adaptador("https://front.test/").generar(documentacion);

        // Assert
        assertThat(enlace).isEqualTo("https://front.test/doc/" + documentacion);
    }
}
