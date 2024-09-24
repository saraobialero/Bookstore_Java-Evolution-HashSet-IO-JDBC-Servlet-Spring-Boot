package org.evpro.bookshopV5.model.DTO.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.evpro.bookshopV5.model.User;
import org.evpro.bookshopV5.model.enums.LoanStatus;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanDTO {
    private Integer id;
    private Set<LoanDetailsDTO> loanDetails;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
}
