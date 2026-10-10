package com.deepali.usercrud.user;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) { this.repository = repository; }

    public UserResponse create(UserRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) throw new DuplicateEmailException(request.email());
        User user = new User(request.name().trim(), request.email().trim().toLowerCase(), request.phone().trim());
        return UserResponse.from(repository.save(user));
    }
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) { return UserResponse.from(findOrThrow(id)); }
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) { return repository.findAll(pageable).map(UserResponse::from); }
    public UserResponse update(Long id, UserRequest request) {
        User user = findOrThrow(id);
        repository.findByEmailIgnoreCase(request.email().trim()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new DuplicateEmailException(request.email());
        });
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(request.phone().trim());
        return UserResponse.from(repository.save(user));
    }
    public void delete(Long id) { repository.delete(findOrThrow(id)); }
    private User findOrThrow(Long id) { return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)); }
}