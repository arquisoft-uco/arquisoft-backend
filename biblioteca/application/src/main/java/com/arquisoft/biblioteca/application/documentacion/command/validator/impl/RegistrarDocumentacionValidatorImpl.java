package com.arquisoft.biblioteca.application.documentacion.command.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.command.validator.RegistrarDocumentacionValidator;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.biblioteca.domain.bibliotecario.model.VigenciaBibliotecario;
import com.arquisoft.biblioteca.domain.bibliotecario.rules.BibliotecarioVigenteRule;
import com.arquisoft.biblioteca.domain.bibliotecario.rules.impl.BibliotecarioVigenteRuleImpl;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.model.UnicidadDocumentacionEntregable;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionDelEntregableUnicaRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.DocumentacionDelEntregableUnicaRuleImpl;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.model.UbicacionArchivoEntregable;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.ArchivoEntregableDisponibleRule;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.EntregableProyectoGradoExisteRule;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.impl.ArchivoEntregableDisponibleRuleImpl;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.impl.EntregableProyectoGradoExisteRuleImpl;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutoresEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.AutoresEntregableVigentesRule;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.EntregableConAutoresRule;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.impl.AutoresEntregableVigentesRuleImpl;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.impl.EntregableConAutoresRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegistrarDocumentacionValidatorImpl implements RegistrarDocumentacionValidator {

    private final EntregableProyectoGradoExisteRule entregableProyectoGradoExisteRule;
    private final DocumentacionDelEntregableUnicaRule documentacionDelEntregableUnicaRule;
    private final ArchivoEntregableDisponibleRule archivoEntregableDisponibleRule;
    private final BibliotecarioVigenteRule bibliotecarioVigenteRule;
    private final EntregableConAutoresRule entregableConAutoresRule;
    private final AutoresEntregableVigentesRule autoresEntregableVigentesRule;

    public RegistrarDocumentacionValidatorImpl() {
        this.entregableProyectoGradoExisteRule = new EntregableProyectoGradoExisteRuleImpl();
        this.documentacionDelEntregableUnicaRule = new DocumentacionDelEntregableUnicaRuleImpl();
        this.archivoEntregableDisponibleRule = new ArchivoEntregableDisponibleRuleImpl();
        this.bibliotecarioVigenteRule = new BibliotecarioVigenteRuleImpl();
        this.entregableConAutoresRule = new EntregableConAutoresRuleImpl();
        this.autoresEntregableVigentesRule = new AutoresEntregableVigentesRuleImpl();
    }

    @Override
    public void validar(RegistroDocumentacionDomain registro, EntregableProyectoGradoDomain entregable,
                        boolean tieneDocumentacion, BibliotecarioDomain bibliotecario,
                        List<AutorDeEntregable> autores) {
        entregableProyectoGradoExisteRule.validar(new ExistenciaEntregableProyectoGrado(
                registro.getEntregableProyectoGrado(), !entregable.esVacio()));
        documentacionDelEntregableUnicaRule.validar(new UnicidadDocumentacionEntregable(
                registro.getEntregableProyectoGrado(), tieneDocumentacion));
        archivoEntregableDisponibleRule.validar(new UbicacionArchivoEntregable(
                registro.getEntregableProyectoGrado(), entregable.getBucket(), entregable.getClaveObjeto()));
        bibliotecarioVigenteRule.validar(new VigenciaBibliotecario(
                registro.getBibliotecario(), !bibliotecario.esVacio(), bibliotecario.estaEliminado()));
        var autoresEntregable = new AutoresEntregable(registro.getEntregableProyectoGrado(), autores);
        entregableConAutoresRule.validar(autoresEntregable);
        autoresEntregableVigentesRule.validar(autoresEntregable);
    }
}
