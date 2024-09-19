package org.evpro.bookshopV5.model.DTO.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String surname;
    private List<Role> roles;
    private boolean active;
}
