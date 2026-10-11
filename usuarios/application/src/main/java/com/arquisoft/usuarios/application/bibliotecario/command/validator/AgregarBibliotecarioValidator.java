package com.arquisoft.usuarios.application.bibliotecario.command.validator;

import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;

import java.util.UUID;

public interface AgregarBibliotecarioValidator {

    void validar(UUID usuario, BibliotecarioDomain bibliotecario);
}
