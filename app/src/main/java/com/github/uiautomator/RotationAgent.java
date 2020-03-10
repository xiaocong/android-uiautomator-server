package com.github.uiautomator;

import android.net.LocalServerSocket;
import android.net.LocalSocket;

import com.github.uiautomator.compat.WindowManagerWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class RotationAgent extends Thread {
    final private WindowManagerWrapper wm = new WindowManagerWrapper();
    private String socketName;
    private PrintWriter writer;

    RotationAgent(String socketName) {
        this.socketName = socketName;
        wm.watchRotation(new WindowManagerWrapper.RotationWatcher() {
            @Override
            public void onRotationChanged(int rotation) {
                System.out.println(rotation + writer.toString());
                if (writer != null) {
                    writer.println(rotation * 90);
                    writer.flush();
                }
            }
        });
    }

    private void manageClientConnection(LocalServerSocket serverSocket) {
        while (true) {
            try  {
                LocalSocket socket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.println(wm.getRotation() * 90);
                writer.flush();
                new Thread(() -> {
                    // 不停的往Client输出当前的Rotation信息
                    try {
                        wm.watchRotation(new WindowManagerWrapper.RotationWatcher() {
                            @Override
                            public void onRotationChanged(int rotation) {
                                writer.println(rotation * 90);
                                writer.flush();
                            }
                        });

                        Scanner in = new Scanner(socket.getInputStream());
                        while (in.hasNextLine()) {
                            System.out.println(in.nextLine());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            System.out.println("@" + socketName + " client connection closed");
                            socket.close();
                        } catch (IOException e) {
                        }
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try (LocalServerSocket serverSocket = new LocalServerSocket(socketName)) {
            System.out.println("Listening on localabstract:" + socketName);
            manageClientConnection(serverSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RotationAgent agent = new RotationAgent("rotation");
        agent.run();
    }
}
