package com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.entity.AutorDocumentacionEntity;

import java.util.List;

public interface AutorDocumentacionOutputPort {

    void registrarTodos(List<AutorDocumentacionEntity> autores);
}
