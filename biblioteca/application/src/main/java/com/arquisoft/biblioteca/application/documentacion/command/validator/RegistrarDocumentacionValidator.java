package com.arquisoft.biblioteca.application.documentacion.command.validator;

import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;

import java.util.List;

public interface RegistrarDocumentacionValidator {

    void validar(RegistroDocumentacionDomain registro, EntregableProyectoGradoDomain entregable,
                 boolean tieneDocumentacion, BibliotecarioDomain bibliotecario, List<AutorDeEntregable> autores);
}
