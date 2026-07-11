import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backendHost } from '../config';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);

  sendMessage(message: string): Observable<{ response: string }> {
    return this.http.post<{ response: string }>(`${backendHost}/chat`, { message });
  }
}
