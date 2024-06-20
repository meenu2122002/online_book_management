package com.mukund.booknetwork.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private  Integer id;
    private String authorName;

    private String isbn;

    private String title;
    private  double rating;
private boolean returned;

private boolean returnApproved;


}
