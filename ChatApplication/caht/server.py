import threading
import socket
import argparse
import os
import mysql.connector
import atexit

class Server(threading.Thread):
    def __init__(self, host='127.0.0.1', port=1060, name='Server'):
        super().__init__()
        self.connections = []
        self.host = host
        self.port = port
        self.name = name
        atexit.register(self.cleanup)

    def run(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            sock.bind((self.host, self.port))
            self.init_db()

            sock.listen(1)
            print("I'm listening at", sock.getsockname())

            while True:
                try:
                    sc, sockname = sock.accept()
                    print(f"Accepting new connection from {sc.getpeername()} to {sc.getsockname()}")
                    
                    server_socket = ServerSocket(sc, sockname, self)
                    server_socket.start()
                    self.connections.append(server_socket)
                    print("Ready to receive messages from", sc.getpeername())
                except Exception as e:
                    print("Error")

    def cleanup(self):
        print("Clearing database...")
        self.clear_database()  # Adatbázis törlése
        print("Server cleanup completed.")
    def init_db(self):
        self.db_conn = mysql.connector.connect(
            host="localhost", 
            user="root", 
            password="",  
            database="chatroom"  #
        )
        self.db_cursor = self.db_conn.cursor()

        # Create table if it doesn't exist
        self.db_cursor.execute('''
            CREATE TABLE IF NOT EXISTS chatroom (
                id INT AUTO_INCREMENT PRIMARY KEY,
                sender VARCHAR(255),
                message TEXT,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        self.db_conn.commit()

    def save_message(self, sender, message):
        try:
            # Replace "\n" with actual newline characters
            message = message.replace("\\n", "\n")
            
            self.db_cursor.execute('INSERT INTO chatroom (sender, message) VALUES (%s, %s)', (sender, message))
            self.db_conn.commit()
           
        except Exception as e:
            print(f"Error saving message: {str(e)}")



    def get_messages(self):
        self.db_cursor.execute('SELECT sender, message FROM chatroom')
        messages = [f"{sender}: {message}" for sender, message in self.db_cursor.fetchall()]
    
        return messages

    

    def broadcast(self, message, source):
        for connection in self.connections:
            if connection.sockname != source:
                connection.send(message)

   

    def remove_connection(self, connection):
        self.connections.remove(connection)

class ServerSocket(threading.Thread):
    
    def __init__(self, sc, sockname, server):
        super().__init__()
        self.sc = sc
        self.sockname = sockname
        self.server = server
    
    def send_previous_messages(self):
        # Fetch previous messages from the database
        previous_messages = self.server.get_messages()
        
        for message in previous_messages:
            # Split the message into separate lines
            message_lines = message.split('\n')
            
            # Send each line individually
            for line in message_lines:
                if line.strip():  #Check its not empty
                    self.send(line + "\n")


    def run(self):
        self.sockname = self.sc.getpeername()
        self.send_previous_messages()  # Send previous messages to the client

        while True:
            try:
                full_message = self.sc.recv(1024).decode('ascii')
                if full_message:
                    # Handling messages 
                    self.server.broadcast(full_message, self.sockname)
                    
                    # Save the message to the database
                    sender, message = self.parse_message(full_message)
                    self.server.save_message(sender, message)
            except socket.error as e:
                print(f"Socket error: {e}")
                break
            except Exception as e:
                print(f"General error: {e}")
                break
           
        self.sc.close()
        self.server.remove_connection(self)

            
    def parse_message(self, full_message):
        # Split the message sender and content
        
        parts = full_message.split(': ', 1)
        if len(parts) == 2:
            return parts[0], parts[1]
        else:
            return self.server.name, full_message
    def send(self, message):
        if message:
            self.sc.sendall(message.encode('ascii'))
    
   

def exit(server):
    while True:
        ipt = input("")
        if ipt == "q":
            print("Clearing database and closing all connections...")
            server.clear_database()  # Clear the database
            for connection in server.connections:
                connection.sc.close()
            
            print("Shutting down server")
            os._exit(0)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Chatroom server")
    parser.add_argument('host', nargs='?', default='127.0.0.1', help='Interface the server listens at')
    parser.add_argument('-p', metavar='PORT', type=int, default=1060, help='TCP port (default 1060)')
    
    args = parser.parse_args()
    
    server = Server(args.host, args.p)
    server.start()
    
    exit_thread = threading.Thread(target=exit, args=(server,))
    exit_thread.start()
