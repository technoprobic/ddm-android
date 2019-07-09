package com.technoprobic.ddm.ddm.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeUnit;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.model.AppDatabase;
import com.technoprobic.ddm.ddm.model.SensorData;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jota.IotaAPI;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Transaction;
import jota.model.Transfer;

import static android.content.Context.MODE_PRIVATE;

public class BuyerListenerServer {

    SensorDataCaptureSessionDetailActivity activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 5678;  // SDPP port

    private AppDatabase mDb;
    private Context mContext;

    private final String TAG = this.getClass().getSimpleName();

    public BuyerListenerServer(Context context) {
        mDb = AppDatabase.getInstance(context);
        mContext = context;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {

        Log.d(TAG, "ondestroy: " + TAG);
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(socketServerPORT);
                Log.d(TAG, getIpAddress());

                while (true) {
                    Socket socket = serverSocket.accept();

                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;

        SocketServerReplyThread(Socket socket) {
            hostThreadSocket = socket;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "";

            // ddm repo/spec doesn't detail the transfer, implemented similar to repo
            // currently retrieves most recent session ID, consider sending/receiving session ID from iota transaction message

            //final String IOTA_NODE_ADDRESS = "node02.iotatoken.nl";
            //final String IOTA_NODE_PORT = "14265";
            final String IOTA_NODE_ADDRESS = "pow1.iota.community"; // todo replace with your own node,
            final String IOTA_NODE_PORT = "443"; // possible candidates with POW and SSL at iota.dance
            IotaAPI iotaAPI = new IotaAPI.Builder()
                    //.protocol("http")
                    .protocol("https")
                    .host(IOTA_NODE_ADDRESS)
                    .port(IOTA_NODE_PORT)
                    .build();

            // retrieve transaction to users Iota address, get ipfshash from transaction,
            // then search for user sessions matching that ipfshash, then confirm value transferred >
            // value set by user and that transaction is recent
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String userIotaAddress = sharedPreferences.getString(mContext.getResources().getString(R.string.settings_iota_seed_key), "");
            //Log.d(TAG, "user iota address: " + userIotaAddress);
            String[] userIotaAddressArray = new String[]{userIotaAddress};
            SensorDataCaptureSession tempSensorDataCaptureSession;
            boolean sessionWithIpfsHash = false;
            boolean isRequiredValue = false;
            long transactionCutoffDate = System.currentTimeMillis() - (DateUtils.HOUR_IN_MILLIS * 12);  // use 12 hour cutoff
            if (!userIotaAddress.equals("")) {
                //retrieve transactions to user's Iota address
                try {
                    List<Transaction> userAddressTransactions = iotaAPI.findTransactionObjectsByAddresses(userIotaAddressArray);
                    for (Transaction currTransaction : userAddressTransactions) {
                        //Log.d(TAG, "iota transaction: " + currTransaction.getTag() + " Value: " + currTransaction.getValue() );
                        //Log.d(TAG, "iota transaction bundle: " + currTransaction.getBundle() + "Timestamp: " + GeneralUtils.getFormattedDateFromMillis(currTransaction.getAttachmentTimestamp())  );
                        // get ipfshash from transaction, assume in bundle/message
                        String transactionBundleIpfsHash = currTransaction.getBundle().trim();
                        if (transactionBundleIpfsHash != null && !transactionBundleIpfsHash.equals("")) {
                            // search for user sessions matching that ipfshash
                            tempSensorDataCaptureSession = mDb.sensorDataCaptureSessionDao().loadSensorDataCaptureSessionByIpfsHash(transactionBundleIpfsHash);
                            //confirm value transferred > value set by user and is recent transaction
                            if (tempSensorDataCaptureSession != null) {
                                sessionWithIpfsHash = true;
                                float requiredValue = tempSensorDataCaptureSession.getUserPrice();
                                if (currTransaction.getValue() > requiredValue
                                        && currTransaction.getAttachmentTimestamp() > transactionCutoffDate ) {
                                    isRequiredValue = true;
                                    break;
                                }
                            }
                        }

                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Iota transaction error: " + e.getMessage());
                } catch (ArgumentException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Iota transaction error: " + e.getMessage());
                }
            }

            // currently retrieves most recent session ID
            long currSessionId;
            tempSensorDataCaptureSession = mDb.sensorDataCaptureSessionDao().loadHighestSessionIdSensorDataCaptureSession();

            // add sensor data to transfer string
            if (tempSensorDataCaptureSession != null) {
                currSessionId = tempSensorDataCaptureSession.getSessionId();

                // limit string size under maximum permitted String length
                final int ABS_MAX_STRING_LENGTH = Integer.MAX_VALUE;
                final int MAX_STRING_LENGTH = (int) (.95 * ABS_MAX_STRING_LENGTH);

                List<SensorData> sensorDataList = mDb.sensorDataDao().loadSensorDataBySessionId(currSessionId);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                for (SensorData currSensorData : sensorDataList) {
                    String json = gson.toJson(currSensorData);
                    msgReply += json + "\n";

                    if (msgReply.length() > MAX_STRING_LENGTH) {
                        break;
                    }
                }

            } else {
                msgReply += mContext.getResources().getString(R.string.no_sensor_data_available);
            }

            // transfer data
            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += msgReply + "\n";

            } catch (IOException e) {
                e.printStackTrace();
                message += mContext.getResources().getString(R.string.server_error_message) + " " + e.toString() + "\n";
            }

            // increment download count for session
            if (tempSensorDataCaptureSession != null) {
                int newDownloadCount = tempSensorDataCaptureSession.getPurchaseCount() + 1;
                tempSensorDataCaptureSession.setPurchaseCount(newDownloadCount);
                mDb.sensorDataCaptureSessionDao().updateSensorDataCaptureSession(tempSensorDataCaptureSession);
            }

            // ddm repo/spec doesn't detail the transfer, following is test response

            // send receipt over Iota
            final String PAYMENT_ADDRESS = "RFQASBVGDTTPDEYVSPIWHG9YUMHAGHFDUZVVXEMDRNNMWJHQYBWHXWQ9JST9NZFBFMFPPFETFLE9RMUJCTNXFZJDGW";
                /*List transfers = new ArrayList<>();
                Transfer transfer = new Transfer(PAYMENT_ADDRESS,
                    0, // 0 value transfer
                     TrytesConverter.asciiToTrytes("Data Invoice"),
                    ""); */

            List<Transfer> transfers = new ArrayList<>();
            //payment_address = iota.Address('RFQASBVGDTTPDEYVSPIWHG9YUMHAGHFDUZVVXEMDRNNMWJHQYBWHXWQ9JST9NZFBFMFPPFETFLE9RMUJCTNXFZJDGW') // from seller_websockets.py
            final String TEST_SEED1 = "IHDEENZYITYVYSPKAURUZAQKGVJEREFDJMYTANNXXGPZ9GJWTEOJJ9IPMXOGZNQLSNMFDSQOTZAEETUEA"; //todo confirm this seed or the _2 address
            final String TEST_ADDRESS_WITHOUT_CHECKSUM_SECURITY_LEVEL_2 = "LXQHWNY9CQOHPNMKFJFIJHGEPAENAOVFRDIBF99PPHDTWJDCGHLYETXT9NPUVSNKT9XDTDYNJKJCPQMZC";
            final String TEST_MESSAGE = "DATAINVOICEIOTATEST";
            final String TEST_TAG = "DDMSDPPBUYER999999999999999";
            final int MIN_WEIGHT_MAGNITUDE = 14;
            final int DEPTH = 9;
            transfers.add(new Transfer(TEST_ADDRESS_WITHOUT_CHECKSUM_SECURITY_LEVEL_2, 0, TEST_MESSAGE, TEST_TAG));
            try {
                SendTransferResponse str = iotaAPI.sendTransfer(TEST_SEED1,
                        2,
                        DEPTH,
                        MIN_WEIGHT_MAGNITUDE,
                        transfers,
                        null,
                        null,
                        false,
                        true,
                        null);
                Log.d(TAG, "Iota transfer complete:" + str.toString());
            } catch (IllegalAccessError e) {
                e.printStackTrace();
                Log.d(TAG, "Iota transfer error illegal:" + e.getMessage());
            } catch (ArgumentException e) {
                e.printStackTrace();
                Log.d(TAG, "Iota transfer error:" + e.getMessage());
            }

            // broadcast successful download
            if (tempSensorDataCaptureSession != null) {
                Intent localIntent = new Intent(BuyerListenerServerService.BROADCAST_DOWNLOAD);
                localIntent.putExtra(BuyerListenerServerService.BROADCAST_DOWNLOAD_USER_MESSAGE_KEY,
                        mContext.getResources().getString(R.string.message_successful_download) + tempSensorDataCaptureSession.getUserSessionDescription());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);
            }

        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at: " + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Error getting IP Address: " + e.toString() + "\n";
        }
        return ip;
    }

}
