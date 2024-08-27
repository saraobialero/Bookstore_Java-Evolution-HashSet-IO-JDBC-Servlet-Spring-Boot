package org.evpro.bookshopV1;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Book {

    private String title;
    private String author;
    private String ISBN;
    private boolean available;



}
