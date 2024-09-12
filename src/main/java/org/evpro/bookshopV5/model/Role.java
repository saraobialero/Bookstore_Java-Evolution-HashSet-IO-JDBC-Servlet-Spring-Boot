package org.evpro.bookshopV5.model;

import jakarta.persistence.*;
import lombok.*;
import org.evpro.bookshopV5.model.enums.RoleCode;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleCode role;

}


