import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent],
  template: `
    @if (auth.isAuthenticated()) {
      <app-navbar />
    }
    <router-outlet />
  `,
  styleUrl: './app.css'
})
export class App {
  auth = inject(AuthService);
}
