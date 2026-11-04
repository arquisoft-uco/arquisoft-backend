package com.arquisoft.usuarios.domain.bibliotecario.model;

import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;

import java.util.UUID;

public record DisponibilidadBibliotecarioUsuario(UUID usuario, String nombre, BibliotecarioDomain bibliotecario) {}
