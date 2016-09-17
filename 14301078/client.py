# coding:utf-8
import socket
import sys

HOST = '127.0.0.1'
PORT = 3333

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

s.connect((HOST, PORT))

senddata = raw_input('输入要传输的字符串:')

s.send(senddata)

data = s.recv(9999)

sys.stdout.write("收到返回字符串：")

sys.stdout.write(data)

s.close()
