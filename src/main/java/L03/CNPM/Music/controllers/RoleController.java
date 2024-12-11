package L03.CNPM.Music.controllers;

import L03.CNPM.Music.models.Role;
import L03.CNPM.Music.responses.ResponseObject;
import L03.CNPM.Music.responses.role.RoleResponse;
import L03.CNPM.Music.services.roles.RoleService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Comparator;

@RestController
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    // ENDPOINT: {{API_PREFIX}}/roles [GET]
    // GET ALL ROLES IN SYSTEM, USE BEFORE LOGIN
    // HEADERS: NO AUTHENTICATION
    // QUERY: NO QUERY
    /*
     * RESPONSE:
     * {
     * "message": "Get roles successfully",
     * "status": "200 OK",
     * "data": [
     * {
     * "id": 1,
     * "name": "ADMIN"
     * },
     * ]
     * }
     */
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();

        roles = roles.stream().sorted(Comparator.comparing(Role::getId)).toList();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get roles successfully")
                .status(HttpStatus.OK)
                .data(roles.stream().map(RoleResponse::fromRole).toList())
                .build());
    }
}
