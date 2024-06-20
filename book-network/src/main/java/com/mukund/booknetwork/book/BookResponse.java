package com.mukund.booknetwork.book;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private  Integer id;
    private String authorName;
    private String owner;
    private String isbn;
    private String synopsis;
    private String title;
    private  byte[] cover;
    private boolean archived;
    private boolean shareable;
    private double rating;



}
