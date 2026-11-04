package com.arquisoft.usuarios.application.bibliotecario.command.validator;

import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarBibliotecarioValidator {

    void validar(UsuarioDomain usuario, BibliotecarioDomain bibliotecario);
}
