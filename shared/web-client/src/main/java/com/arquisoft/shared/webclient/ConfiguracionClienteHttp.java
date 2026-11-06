package com.arquisoft.shared.webclient;

import java.time.Duration;

public record ConfiguracionClienteHttp(String urlBase, Duration connectTimeout, Duration readTimeout) {
}
