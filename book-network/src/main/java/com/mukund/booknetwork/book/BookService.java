package com.mukund.booknetwork.book;


import com.mukund.booknetwork.book.exception.OperationNotPermittedException;
import com.mukund.booknetwork.book.file.FileStorageService;
import com.mukund.booknetwork.user.User;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;
//    private BookSpecification spec;

    public Integer save(BookRequest request, Authentication connectedUser){
        User user=(User)connectedUser.getPrincipal();
        Book book =bookMapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();



    }


    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No Book with Specified ID exist"));

    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {

        User user=(User)connectedUser.getPrincipal();
        System.out.println("Connected User Name in findAllBooks Service");
        System.out.println(user.fullName());
        System.out.println(user.getId());
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Book> books=bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse>bookResponse=books.stream().map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
           bookResponse,

                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );

    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user=(User)connectedUser.getPrincipal();

        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Book> books=bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);

        List<BookResponse>bookResponse=books.stream().map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );




    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {

        User user=(User)connectedUser.getPrincipal();

        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
Page<BookTransactionHistory>borrowedBooks=bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());

List<BorrowedBookResponse>borrowedBookResponses=borrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                borrowedBookResponses,
                borrowedBooks.getNumber(),
                borrowedBooks.getSize(),
                borrowedBooks.getTotalElements(),
                borrowedBooks.getTotalPages(),
                borrowedBooks.isFirst(),
                borrowedBooks.isLast()

        );



    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {

        User user=(User)connectedUser.getPrincipal();

        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<BookTransactionHistory>borrowedBooks=bookTransactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());

        List<BorrowedBookResponse>borrowedBookResponses=borrowedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(
                borrowedBookResponses,
                borrowedBooks.getNumber(),
                borrowedBooks.getSize(),
                borrowedBooks.getTotalElements(),
                borrowedBooks.getTotalPages(),
                borrowedBooks.isFirst(),
                borrowedBooks.isLast()


        );




    }

    public Integer updateShareable(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with Specified Id found!"));

        User user=(User)connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("Not Enough Permissions  to change Shareable Settings");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return book.getId();
    }

    public Integer updateArchived(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with Specified Id found!"));

        User user=(User)connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("Not Enough Permissions  to change Archive Settings");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();

    }


    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book =bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with Specified ID does not exist"));

   if(book.isArchived() || !book.isShareable()){
       throw  new OperationNotPermittedException("The requested book cannot be borrowed as Either it is Archived or Not Shareable ");
   }

   User user=(User)connectedUser.getPrincipal();
   if(Objects.equals(user.getId(),book.getOwner().getId())){
       throw new OperationNotPermittedException("You cannot borrow your own book");
   }
   final boolean isAlreadyBorrowed=bookTransactionHistoryRepository.isAlreadyBorrowed(bookId,user.getId());
   if(isAlreadyBorrowed){
       throw new OperationNotPermittedException("Book is Already Borrowed");
   }
   BookTransactionHistory bookTransactionHistory=BookTransactionHistory.builder()
           .user(user)
           .book(book)
           .returnApproved(false)
           .returned(false)
           .build();
 return   bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with Specified Id found!"));

        if(book.isArchived() || !book.isShareable()){
            throw  new OperationNotPermittedException("The requested book cannot be returned as Either it is Archived or Not Shareable ");
        }
        User user=(User)connectedUser.getPrincipal();
        if(Objects.equals(user.getId(),book.getOwner().getId())){
            throw new OperationNotPermittedException("You cannot return your own book");
        }
        BookTransactionHistory bookTransactionHistory=bookTransactionHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));


        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

    }


    public Integer approveBookReturn(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with Specified Id found!"));

        if(book.isArchived() || !book.isShareable()){
            throw  new OperationNotPermittedException("The requested book cannot be returned as Either it is Archived or Not Shareable ");
        }
        User user=(User)connectedUser.getPrincipal();
        if(!Objects.equals(user.getId(),book.getOwner().getId())){
            throw new OperationNotPermittedException("You cannot Approve return of book that you dont own");
        }
        BookTransactionHistory bookTransactionHistory=bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId,user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("Book is not returned yet so you cannot approve its return"));
bookTransactionHistory.setReturnApproved(true);
return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();



    }

    public void uploadBookCoverPicture(Integer bookId, Authentication connectedUser, MultipartFile file) {
        Book book=bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with Specified Id found!"));
        User user=(User)connectedUser.getPrincipal();
        var bookCover=fileStorageService.saveFile(file,bookId,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}
