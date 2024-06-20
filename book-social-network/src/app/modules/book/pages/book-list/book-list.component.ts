import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {BookResponse} from "../../../../services/models/book-response";
// import {isPackageNameSafeForAnalytics} from "@angular/cli/src/analytics/analytics";


@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent implements OnInit{
 page: number = 0;
  size: number=3;
  bookResponse:PageResponseBookResponse={};
  public  message: string="";
  level:string="success";




  constructor(
    private bookService :BookService,
    private router:Router
  ) {
  }

  ngOnInit(): void {
        this.findAllBooks();
    }


  private findAllBooks() {
    this.bookService.findAllBooks({
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

  borrowBook(book: BookResponse) {
    this.message='';
    this.bookService.borrowBook(({
      'book-id':book.id as number
    })).subscribe({
      next:()=>{
        this.level="success";
        this.message="Book successfully added to your list";
      },
      error:(error)=>{
        console.log(error);
        this.level='error';
        this.message=error.error.error;
    }
    })
  }

  // protected readonly isPackageNameSafeForAnalytics = isPackageNameSafeForAnalytics;
}
