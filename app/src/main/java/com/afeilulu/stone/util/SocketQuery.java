package com.afeilulu.stone.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketQuery<E> extends Thread {
    private final static String TAG = "SocketQuery";
    Query<E> data;
    private IPEntry ip;

    public SocketQuery(IPEntry ip, Query<E> data) {
        this.ip = ip;
        this.data = data;
    }

    @Override
    public void run() {
        DataInputStream dataInput = null;
        DataOutputStream dataOutput = null;
        Socket socketClient = null;
        try {
            if (ip == null) {
                return;
            }
            socketClient = new Socket();
            socketClient.connect(
                    new InetSocketAddress(ip.getHost(), ip.getPort()), 60000);
            socketClient.setSoTimeout(120000);
            dataOutput = new DataOutputStream(socketClient.getOutputStream());
            dataInput = new DataInputStream(socketClient.getInputStream());
            E e = data.process(dataInput, dataOutput);
            data.callback(e);
        } catch (Exception e) {
            data.callback(null);
//            Log.e(TAG, "query", e);
        } finally {
//            Log.i(TAG, "ip:" + ip);
            if (dataInput != null) {
                try {
                    dataInput.close();
                } catch (IOException e) {
                }
            }
            if (dataOutput != null) {
                try {
                    dataOutput.close();
                } catch (IOException e) {
                }
            }
            if (socketClient != null) {
                try {
                    socketClient.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public interface Query<E> {
        public E process(DataInputStream in, DataOutputStream out)
                throws Exception;

        public void callback(E e);
    }
}
