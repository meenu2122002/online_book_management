package com.mukund.booknetwork.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory,Integer> {



    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id=:userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);


    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id=:userId
           
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);


    @Query("""
            SELECT
            (COUNT(*)>0) AS isBorrowed
            FROM BookTransactionHistory history
            WHERE history.book.id=:bookId
            AND history.user.id=:userId
            AND history.returnApproved=false
       
            """)
    boolean isAlreadyBorrowed(Integer bookId, Integer userId);



    @Query("""


            SELECT transaction 
            FROM BookTransactionHistory transaction
            WHERE transaction.user.id=:userId
            AND transaction.book.id=:bookId
            AND transaction.returned=false
            AND transaction.returnApproved=false
            """)
    Optional< BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);


    @Query("""
            
            
            SELECT transaction 
            FROM BookTransactionHistory transaction
            WHERE transaction.book.owner.id=:userId
            AND transaction.book.id=:bookId
            AND transaction.returned=true
            AND transaction.returnApproved=false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);
}