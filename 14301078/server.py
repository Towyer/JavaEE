# coding:utf-8
import socket
import thread
import sys

def main():

    HOST = '127.0.0.1'
    PORT = 3333

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    sys.stdout.write('服务器正在启动...')
    s.bind((HOST, PORT))
    sys.stdout.write('成功！\n')

    s.listen(10)

    sys.stdout.write("客户端现在可以连接。\n")

    while True:
        connect, address = s.accept()
        sys.stdout.write("-收到一个客户端连接...\n")
        thread.start_new_thread(Client, (connect, address))

def Client(connect, address):

    data = connect.recv(9999)

    sys.stdout.write("-收到了字符串:")
    sys.stdout.write(data)
    sys.stdout.write("\n")
    sys.stdout.write("-接收字符串完成。\n")

    connect.sendall(data[::-1])

    sys.stdout.write("-字符串已完成处理并反馈到客户端。\n")

    connect.close()
    sys.stdout.write("-一个客户端已断开连接。\n")

if __name__ == "__main__":
    main()
