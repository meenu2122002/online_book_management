import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {BookResponse} from "../../../../services/models/book-response";
// import {isPackageNameSafeForAnalytics} from "@angular/cli/src/analytics/analytics";


@Component({
  selector: 'app-my-books',
  templateUrl: './my-books.component.html',
  styleUrl: './my-books.component.scss'
})
export class MyBooksComponent implements OnInit{
  page: number = 0;
  size: number=3;
  bookResponse:PageResponseBookResponse={};





  constructor(
    private bookService :BookService,
    private router:Router
  ) {
  }

  ngOnInit(): void {
    this.findAllBooks();
  }


  private findAllBooks() {
    this.bookService.findAllBooksByOwner({
      page:this.page,
      size:this.size
    }).subscribe({
      next:(books=>{
        this.bookResponse=books;
        console.log(this.bookResponse.totalPages+" totalpages "+ this.bookResponse.size);
      })
    })
  }

  goToThePreviousPage() {
    this.page--;
    this.findAllBooks();
  }

  goToTheFirstPage() {
    this.page=0;
    this.findAllBooks();
  }

  goToPage(page: number) {
    this.page=page;
    this.findAllBooks();
  }

  goToTheNextPage() {
    this.page++;
    this.findAllBooks();
  }

  goToTheLastPage() {
    this.page=this.bookResponse.totalPages as number-1;
    this.findAllBooks();
  }


  isLastPage() :boolean{
    return this.page==this.bookResponse.totalPages as number -1;
  }



  // protected readonly isPackageNameSafeForAnalytics = isPackageNameSafeForAnalytics;
  archiveBook(book: BookResponse) {
this.bookService.updateArchivedStatus({
  'book-id':book.id as number
}).subscribe({
  next:()=>{
    book.archived=!book.archived;
  }
})


  }
  shareBook(book: BookResponse) {
this.bookService.updateShareableStatus({
  'book-id':book.id as number
}).subscribe({
  next:()=>{
    book.shareable=!book.shareable;

  }
})

  }

  editBook(book: BookResponse) {
this.router.navigate(['books','manage',book.id]);


  }
}
