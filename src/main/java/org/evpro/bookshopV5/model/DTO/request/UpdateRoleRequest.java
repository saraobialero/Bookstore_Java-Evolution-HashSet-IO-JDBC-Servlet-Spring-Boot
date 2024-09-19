
package org.evpro.bookshopV5.model.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.evpro.bookshopV5.model.enums.RoleCode;

@Data
public class UpdateRoleRequest {

    @NotBlank(message = "Role is required")
    private RoleCode role;
}
