package com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.entity.AutorDocumentacionEntity;
import com.arquisoft.biblioteca.domain.autordocumentacion.AutorDocumentacionDomain;

public final class AutorDocumentacionMapper {

    private AutorDocumentacionMapper() {}

    public static AutorDocumentacionEntity toEntity(AutorDocumentacionDomain autorDocumentacion) {
        return new AutorDocumentacionEntity(
                autorDocumentacion.getId(), autorDocumentacion.getAutor(), autorDocumentacion.getDocumentacion());
    }
}
