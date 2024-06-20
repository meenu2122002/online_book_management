package com.mukund.booknetwork.book;


//import org.apache.tomcat.util.http.fileupload.FileUtils;
import com.mukund.booknetwork.book.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {


    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();

    }

    public BookResponse toBookResponse(Book book) {
        System.out.println("toBookResponse");
        System.out.println(book.getOwner().getId());


        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .archived(book.isArchived())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .rating(book.getRating())
                .shareable(book.isShareable())
                .synopsis(book.getSynopsis())
                .owner(book.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory bookTransactionHistory) {
        return BorrowedBookResponse.builder()
                .id(bookTransactionHistory.getBook().getId())
                .title(bookTransactionHistory.getBook().getTitle())
                .returnApproved(bookTransactionHistory.isReturnApproved())
                .returned(bookTransactionHistory.isReturned())
                .isbn(bookTransactionHistory.getBook().getIsbn())
                .rating(bookTransactionHistory.getBook().getRating())
                .authorName(bookTransactionHistory.getBook().getAuthorName())
                .build();

    }
}
