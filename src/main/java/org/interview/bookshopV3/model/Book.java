package org.interview.bookshopV3.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;


@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Book implements Serializable {

    private Integer id;
    private String title;
    private String author;
    private Date publicationYear;
    private String description;
    private String ISBN;
    private boolean available;

    @Override
    public String toString() {
        return String.format("Book{id='%s', title='%s', author='%s', publication_year='%s' description='%s' ISBN='%s', available=%s}",
                id, title, author, publicationYear,  description, ISBN, available);
    }

}
