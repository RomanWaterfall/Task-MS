package com.itm.space.backendresources.service;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest extends BaseIntegrationTest {

    private final UserService userService;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @MockBean
    private Keycloak keycloak;

    @MockBean
    private RealmResource realmResource;

    @MockBean
    private UsersResource usersResource;


    @MockBean
    private RoleMappingResource roleMappingResource;

    @MockBean
    private MappingsRepresentation mappingsRepresentation;

    @BeforeEach
    public void setup() {
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    public void createUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("Popka", "Popov@mail.com", "12345", "John", "Doe");
        Response response = Response.status(Response.Status.CREATED).location(new URI("user_id")).build();
        when(usersResource.create(any())).thenReturn(response);
        userService.createUser(userRequest);
        verify(usersResource, times(1)).create(any());
    }

    @Test
    public void testGetUserById() {

        UserRepresentation userRepresentation = new UserRepresentation();
        UUID userId = UUID.randomUUID();
        userRepresentation.setId(String.valueOf(userId));
        userRepresentation.setFirstName("Bob");

        when(usersResource.get(anyString())).thenReturn(mock(UserResource.class));
        when(keycloak.realm(anyString()).users().get(anyString()).toRepresentation()).thenReturn(userRepresentation);
        when(keycloak.realm(anyString()).users().get(anyString()).roles()).thenReturn(roleMappingResource);
        when(keycloak.realm(anyString()).users().get(anyString()).roles().getAll()).thenReturn(mappingsRepresentation);

        UserResponse response = userService.getUserById(userId);
        assertEquals("Bob", response.getFirstName());
    }
}