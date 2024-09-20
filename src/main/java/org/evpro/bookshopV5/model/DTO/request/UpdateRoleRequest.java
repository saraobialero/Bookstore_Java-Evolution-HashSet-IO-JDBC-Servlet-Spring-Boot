
package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.evpro.bookshopV5.model.Role;
import org.evpro.bookshopV5.model.enums.RoleCode;

import java.util.List;

@Data
public class UpdateRoleRequest {
    @NotEmpty(message = "At least one role is required")
    private List<RoleCode> roleCodes;
}
