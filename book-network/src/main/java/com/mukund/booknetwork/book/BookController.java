package com.mukund.booknetwork.book;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {




    private final BookService bookService;
    @PostMapping()
    public ResponseEntity<Integer> saveBook(
            @RequestBody @Valid BookRequest request,
          Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.save(request,connectedUser));


    }




    @GetMapping("{book_id}")
    public  ResponseEntity<BookResponse> findBookById(
            @PathVariable("book_id") Integer bookId
    ){
      return ResponseEntity.ok(bookService.findById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name="page",defaultValue = "0",required = false) int page,
            @RequestParam(name="size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){

        return ResponseEntity.ok(bookService.findAllBooks(page,size,connectedUser));
    }



    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name="page",defaultValue = "0",required = false) int page,
            @RequestParam(name="size",defaultValue = "10",required = false) int size,
            Authentication connectedUser

    ){

        return ResponseEntity.ok(bookService.findAllBooksByOwner(page,size,connectedUser));

    }



    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name="page",defaultValue = "0",required = false) int page,
            @RequestParam(name="size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page,size,connectedUser));
    }


    @GetMapping("/returned")
public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name="page",defaultValue = "0",required = false) int page,
            @RequestParam(name="size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page,size,connectedUser));
    }


    @PatchMapping("/shareable/{book_id}")
    public ResponseEntity<Integer> updateShareable(
       @PathVariable("book_id") Integer bookId,
       Authentication connectedUser
    ){

        return ResponseEntity.ok(bookService.updateShareable(bookId,connectedUser));
    }
    @PatchMapping("/archived/{book_id}")
    public ResponseEntity<Integer> updateArchived(
       @PathVariable("book_id") Integer bookId,
       Authentication connectedUser
    ){

        return ResponseEntity.ok(bookService.updateArchived(bookId,connectedUser));
    }

    @PostMapping("/borrow/{book_id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));
    }

    @PatchMapping("/borrow/return/{book_id}")
    public ResponseEntity<Integer> returnBorrowedBook(

            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId,connectedUser));
    }

    @PatchMapping("/borrow/return/approve/{book_id}")
    public ResponseEntity<Integer> handleReturnApprove(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.approveBookReturn(bookId,connectedUser));
    }

    @PostMapping(value = "/cover/{book_id}",consumes = "multipart/form-data")
    public ResponseEntity<?> handleBookCoverPictureUpload(
            @PathVariable("book_id") Integer bookId,
            Authentication connectedUser,
            @Parameter()
            @RequestPart("file") MultipartFile file


            ){
        bookService.uploadBookCoverPicture(bookId,connectedUser,file);
        return ResponseEntity.accepted().build();
    }




}