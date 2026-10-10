package com.deepali.usercrud.user;

import com.deepali.usercrud.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Authenticated user CRUD endpoints")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @PostMapping @Operation(summary = "Create user")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User created successfully", service.create(request)));
    }
    @GetMapping("/{id}") @Operation(summary = "Get user by ID")
    public ApiResponse<UserResponse> get(@PathVariable Long id) { return ApiResponse.ok("User retrieved successfully", service.getById(id)); }
    @GetMapping @Operation(summary = "List users (paginated)")
    public ApiResponse<Page<UserResponse>> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ApiResponse.ok("Users retrieved successfully", service.getAll(pageable));
    }
    @PutMapping("/{id}") @Operation(summary = "Update user")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.ok("User updated successfully", service.update(id, request));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", null));
    }
}