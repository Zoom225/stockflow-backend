package com.stockflow.service;

import com.stockflow.dto.request.UpdateUserRequest;
import com.stockflow.dto.response.UserResponse;
import java.util.List;

public interface UserService {

	List<UserResponse> getAllUsers();

	UserResponse getUserById(Long id);

	UserResponse updateUser(Long id, UpdateUserRequest request);

	void deleteUser(Long id);
}
