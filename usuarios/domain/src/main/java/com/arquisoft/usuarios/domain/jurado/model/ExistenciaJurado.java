package com.arquisoft.usuarios.domain.jurado.model;

import com.arquisoft.usuarios.domain.jurado.JuradoDomain;

import java.util.UUID;

public record ExistenciaJurado(UUID usuario, JuradoDomain jurado) {}
