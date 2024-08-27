package org.evpro.bookshopV2;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Book implements Serializable {

    private String title;
    private String author;
    private String ISBN;
    private boolean available;



}
