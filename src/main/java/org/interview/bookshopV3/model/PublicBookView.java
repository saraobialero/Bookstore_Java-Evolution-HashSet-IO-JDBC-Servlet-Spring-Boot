package org.interview.bookshopV3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
public  class PublicBookView {
    private String title;
    private String author;
    private Date publicationYear;
    private String description;
    private String ISBN;
}
