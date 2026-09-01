package com.stockflow.mapper;

import com.stockflow.dto.request.UpdateUserRequest;
import com.stockflow.dto.response.UserResponse;
import com.stockflow.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public void updateEntity(AppUser user, UpdateUserRequest request) {
		user.setFullName(request.fullName().trim());
		user.setEmail(request.email().trim().toLowerCase());
	}

	public UserResponse toResponse(AppUser user) {
		return new UserResponse(
				user.getId(),
				user.getFullName(),
				user.getEmail(),
				user.getRole().name(),
				user.getCreatedAt(),
				user.getUpdatedAt()
		);
	}
}
