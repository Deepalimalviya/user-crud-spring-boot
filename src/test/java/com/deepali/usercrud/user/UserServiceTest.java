package com.deepali.usercrud.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository repository;
    @InjectMocks UserService service;

    @Test void createReturnsSavedUser() {
        UserRequest request = new UserRequest("Deepali", "Deepali@example.com", "9876543210");
        when(repository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        UserResponse response = service.create(request);
        assertEquals("Deepali", response.name());
        assertEquals("deepali@example.com", response.email());
        verify(repository).save(any(User.class));
    }

    @Test void createRejectsDuplicateEmail() {
        when(repository.existsByEmailIgnoreCase("a@example.com")).thenReturn(true);
        assertThrows(DuplicateEmailException.class, () -> service.create(new UserRequest("A", "a@example.com", "123")));
        verify(repository, never()).save(any());
    }

    @Test void getMissingUserThrowsNotFound() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getById(77L));
    }

    @Test void listMapsEntitiesToResponses() {
        User user = new User("A", "a@example.com", "123");
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));
        Page<UserResponse> result = service.getAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("A", result.getContent().get(0).name());
    }

    @Test void deleteMissingUserThrowsNotFound() {
        when(repository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.delete(3L));
        verify(repository, never()).delete(any(User.class));
    }
}