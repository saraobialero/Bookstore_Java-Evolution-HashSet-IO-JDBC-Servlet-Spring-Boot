package org.evpro.bookshopV4.model;

import lombok.*;

import java.sql.Date;

//POJO (Plain Old Java Objects) class
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserHasBook {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private int quantity;
    private Date borrowDate;
    private Date returnDate;
}
