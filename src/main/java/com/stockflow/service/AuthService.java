package com.stockflow.service;

import com.stockflow.dto.request.LoginRequest;
import com.stockflow.dto.request.RegisterRequest;
import com.stockflow.dto.response.AuthResponse;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);
}
