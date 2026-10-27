package com.arquisoft.usuarios.application.bibliotecario.command.validator;

import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverBibliotecarioValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, BibliotecarioDomain bibliotecario);
}
