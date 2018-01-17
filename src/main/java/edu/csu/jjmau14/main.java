package edu.csu.jjmau14;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            serve();
        }, "Server Thread").start();
        new Thread(() -> {
            client();
        }, "Client Thread").start();
    }

    public static void serve(){
        try (
                ServerSocket ss = new ServerSocket(5000)
        ){
            while(true){
                Socket socket = ss.accept();
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                System.out.println("[" + Thread.currentThread().getName() + "] Server received \"" +
                        dIn.readUTF() + "\" from client.");
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                Thread.sleep(500);
                System.out.println(socket);
                dOut.write(1);
                dOut.write(2);
                dOut.write(3);
                dOut.write(4);
                socket.close();
            }
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

    public static void client() {
        try {
            while(true) {
                Socket socket = new Socket("localhost", 5000);
                System.out.print("[" + Thread.currentThread().getName() + "] Enter your message: ");
                Scanner scnr = new Scanner(System.in);
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                dOut.writeUTF(scnr.nextLine());
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                System.out.println("[" + Thread.currentThread().getName() + "] Received status of " +
                        dIn.read() + " from server");
                System.out.println("[" + Thread.currentThread().getName() + "] Received status of " +
                        dIn.read() + " from server");
                System.out.println("[" + Thread.currentThread().getName() + "] Received status of " +
                        dIn.read() + " from server");
                System.out.println("[" + Thread.currentThread().getName() + "] Received status of " +
                        dIn.read() + " from server");
            }
        } catch (Exception e){
            System.out.println("[" + Thread.currentThread().getName() + "] Error: " + e.getMessage());
        }
    }

}
