import { Component } from '@angular/core';
import {AuthenticationRequest} from "../../services/models/authentication-request";
import {authenticate} from "../../services/fn/authentication/authenticate";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {AuthenticationResponse} from "../../services/models/authentication-response";
import {TokenService} from "../../services/token/token.service";
// import {error} from "@angular/compiler-cli/src/transformers/util";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
authRequest:AuthenticationRequest={email:'',password:''};
errorMsg: Array<string>=[];


constructor(
  private router:Router,
  private authService:AuthenticationService,
private tokenService:TokenService
//   another service
) {
}



  protected readonly authenticate = authenticate;

  login() {
this.errorMsg=[];
this.authService.authenticate({
  body:this.authRequest
}).subscribe({
  next:(res:AuthenticationResponse):void=>{
  //   save the token
    this.tokenService.token=res.token as string;

    this.router.navigate(['books'])
  },
  error: (err) => {
    console.log(err);
    if (err.error.validationErrors) {
      this.errorMsg = err.error.validationErrors;
    } else {
      this.errorMsg.push(err.error.errorMsg);
    }
  }
})
  }

  register() {
this.router.navigate(["register"])
  }
}
