import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ChatService } from '../../services/chat.service';

interface ChatMessage {
  from: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chat',
  imports: [ReactiveFormsModule],
  template: `
    <div class="max-w-3xl mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">Chatbot</h1>

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 flex flex-col h-[32rem]">
        <div class="flex-1 overflow-y-auto p-6 space-y-4">
          @if (messages().length === 0) {
            <p class="text-center text-slate-400 text-sm mt-8">
              Posez une question à l'assistant E-Bank.
            </p>
          }
          @for (message of messages(); track $index) {
            <div class="flex" [class]="message.from === 'user' ? 'justify-end' : 'justify-start'">
              <div
                class="max-w-[75%] rounded-2xl px-4 py-2.5 text-sm whitespace-pre-wrap"
                [class]="
                  message.from === 'user'
                    ? 'bg-indigo-600 text-white rounded-br-sm'
                    : 'bg-slate-100 text-slate-800 rounded-bl-sm'
                "
              >
                {{ message.text }}
              </div>
            </div>
          }
          @if (loading()) {
            <div class="flex justify-start">
              <div class="bg-slate-100 rounded-2xl rounded-bl-sm px-4 py-2.5 text-sm text-slate-500">
                <span class="inline-flex gap-1 items-center">
                  L'assistant écrit
                  <span class="animate-bounce">.</span>
                  <span class="animate-bounce [animation-delay:0.15s]">.</span>
                  <span class="animate-bounce [animation-delay:0.3s]">.</span>
                </span>
              </div>
            </div>
          }
        </div>

        <form
          [formGroup]="form"
          (ngSubmit)="send()"
          class="flex gap-3 border-t border-slate-200 p-4"
        >
          <input
            type="text"
            formControlName="message"
            placeholder="Écrivez votre message..."
            autocomplete="off"
            class="flex-1 rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <button
            type="submit"
            [disabled]="form.invalid || loading()"
            class="rounded-lg bg-indigo-600 text-white text-sm font-semibold px-5 py-2 hover:bg-indigo-700 disabled:opacity-50 transition"
          >
            Envoyer
          </button>
        </form>
      </div>
    </div>
  `
})
export class ChatComponent {
  private chatService = inject(ChatService);
  private fb = inject(FormBuilder);

  messages = signal<ChatMessage[]>([]);
  loading = signal(false);

  form = this.fb.nonNullable.group({
    message: ['', Validators.required]
  });

  send(): void {
    const text = this.form.getRawValue().message.trim();
    if (!text || this.loading()) return;
    this.messages.update((list) => [...list, { from: 'user', text }]);
    this.form.reset({ message: '' });
    this.loading.set(true);
    this.chatService.sendMessage(text).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.messages.update((list) => [...list, { from: 'bot', text: res.response }]);
      },
      error: () => {
        this.loading.set(false);
        this.messages.update((list) => [
          ...list,
          { from: 'bot', text: "Désolé, une erreur est survenue. Veuillez réessayer." }
        ]);
      }
    });
  }
}
