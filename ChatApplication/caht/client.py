import threading
import socket
import argparse
import os
import sys
import tkinter as tk
from tkinter import simpledialog

class Send(threading.Thread):
    def __init__(self, sock, name, update_gui_callback):
        super().__init__()
        self.sock = sock
        self.name = name
        self.update_gui_callback = update_gui_callback

    def run(self):
        while True:
            message = input(f"{self.name}: ")
            self.sock.sendall(f'{message}\n'.encode('ascii'))  # Send message content only
            self.update_gui_callback(f"{self.name}: {message}\n")  # Update GUI with sender's name


class Receive(threading.Thread):
    def __init__(self, sock, update_gui_callback):
        super().__init__()
        self.sock = sock
        self.update_gui_callback = update_gui_callback

    def run(self):
        while True:
            try:
                received_data = self.sock.recv(1024).decode('ascii')
                if received_data:
                    self.update_gui_callback(received_data)
                else:
                    break
            except OSError:
                break
    def display_messages(self, data):
        def insert_message(message):
            self.messages.insert(tk.END, message)
            self.messages.see(tk.END)  # Scroll to the end to show the latest message

        # Split the data by newline characters and insert each message separately
        messages = data.split('\n')
        for message in messages:
            if message.strip():  # Avoid inserting empty or whitespace-only lines
                insert_message(message.strip() + '\n')




class Client:
    def __init__(self, host, port, messages):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.name = None
        self.messages = messages

    def request_name(self):
        root = tk.Tk()
        root.withdraw()
        self.name = simpledialog.askstring("Username", "Enter your name:", parent=root)
        root.destroy()

    def start(self):
        try:
            self.sock.connect((self.host, self.port))
            print("Successfully connected to {}:{}".format(self.host, self.port))
        except socket.error as e:
            print(f"Failed to connect to {self.host}:{self.port}. Error: {e}")
            sys.exit(1)

        send_thread = Send(self.sock, self.name, self.update_gui_with_message)
        receive_thread = Receive(self.sock, self.update_gui_with_message)

        send_thread.start()
        receive_thread.start()

    def update_gui_with_message(self, message):
        def update():
            message_lines = message.split('\n')
            for line in message_lines:
                if line.strip():  # Ensure not to insert empty lines
                    self.messages.insert(tk.END, line.strip())

        self.messages.after(0, update)
    def send_message(self, textInput):
        message = textInput.get()
        textInput.delete(0, tk.END)
        if message.strip():
            full_message = f'{self.name}: {message}\n'
            self.sock.sendall(full_message.encode('ascii'))
            self.update_gui_with_message(full_message)  # Update GUI with own message

           

def main(host, port):
    window = tk.Tk()
    window.title("Main Chatroom")

    fromMessage = tk.Frame(master=window)
    scrollBar = tk.Scrollbar(master=fromMessage)
    messages = tk.Listbox(master=fromMessage, yscrollcommand=scrollBar.set)
    scrollBar.pack(side=tk.RIGHT, fill=tk.Y, expand=False)
    messages.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
    fromMessage.grid(row=0, column=0, columnspan=2, sticky="nsew")

    fromEntry = tk.Frame(master=window)
    textInput = tk.Entry(master=fromEntry)
    textInput.pack(fill=tk.BOTH, expand=True)
    textInput.bind("<Return>", lambda x: client.send_message(textInput))

    sendButton = tk.Button(master=window, text="Send", command=lambda: client.send_message(textInput))
    fromEntry.grid(row=1, column=0, pady=10, padx=10, sticky="ew")
    sendButton.grid(row=1, column=1, pady=10, padx=10, sticky="ew")

    client = Client(host, port, messages)
    client.request_name()
    client.start()

    window.rowconfigure(0, minsize=500, weight=1)
    window.rowconfigure(1, minsize=50, weight=0)
    window.columnconfigure(0, minsize=500, weight=1)
    window.columnconfigure(1, minsize=200, weight=0)

    window.mainloop()

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Chatroom client")
    parser.add_argument('host', nargs='?', default='localhost', help='Interface the client connects to')
    parser.add_argument('-p', metavar='PORT', type=int, default=1060, help='TCP port (default 1060)')

    args = parser.parse_args()
    main(args.host, args.p)
