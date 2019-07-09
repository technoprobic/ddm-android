package com.technoprobic.ddm.ddm.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.contracts.DDMV2Contract;
import com.technoprobic.ddm.ddm.model.AppDatabase;
import com.technoprobic.ddm.ddm.model.InfuraIpfsResponse;
import com.technoprobic.ddm.ddm.model.MarketplaceRecord;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.utils.AppExecutors;
import com.technoprobic.ddm.ddm.utils.BuyerListenerServer;
import com.technoprobic.ddm.ddm.utils.BuyerListenerServerService;
import com.technoprobic.ddm.ddm.utils.GeneralUtils;
import com.technoprobic.ddm.ddm.utils.RetrofitUploadJsonToIpfs;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

//import io.ipfs.api.IPFS;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SensorDataCaptureSessionDetailFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String BUNDLE_KEY = "SensorDataCaptureSession";

    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    private SensorDataCaptureSession sensorDataCaptureSession;

    private TextView tvUserSessionDescription;
    private TextView tvSensorName;
    private TextView tvSessionStartTime;
    private TextView tvSessionStopTime;

    private TextView tvPriorMarketplacePosts;
    private TextView tvDownloadCount;

    private EditText etMarketplaceSeller;
    private EditText etMarketplaceSensorDescription;
    private EditText etMarketplaceSessionDescription;
    private EditText etMarketplaceLatitude;
    private EditText etMarketplaceLongitude;
    private EditText etMarketplacePrice;
    private EditText etMarketplaceIpAddress;
    private EditText etMarketplacePublicIpAddress;
    private Button btnRegisterToMarketplace;
    private ProgressBar progressBar;
    private TextView tvUserMessageContractResponse;
    //private String ipfsHash;

    private Credentials credentials;
    private String ethereumPrivateKey;
    private String ddmContractAddressRopsten;
    private BigInteger gasLimit;
    private BigInteger gasPrice;

    private CoordinatorLayout coordinatorLayout;

    private AppDatabase mDb;


    public SensorDataCaptureSessionDetailFragment() {
        // Required empty public constructor
    }

    public static SensorDataCaptureSessionDetailFragment newInstance(String param1, String param2) {
        SensorDataCaptureSessionDetailFragment fragment = new SensorDataCaptureSessionDetailFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor_data_capture_session_detail, container, false);

        setHasOptionsMenu(true);

        tvUserSessionDescription = rootView.findViewById(R.id.tv_detail_userSessionDescription);
        tvSensorName = rootView.findViewById(R.id.tv_detail_sessionSensorName);
        tvSessionStartTime = rootView.findViewById(R.id.tv_detail_sessionStartTime);
        tvSessionStopTime = rootView.findViewById(R.id.tv_detail_sessionStopTime);

        if (getArguments() != null) {
            sensorDataCaptureSession = getArguments().getParcelable(BUNDLE_KEY);
            tvUserSessionDescription.setText(sensorDataCaptureSession.getUserSessionDescription());
            tvSensorName.setText(sensorDataCaptureSession.getSensorName());
            tvSessionStartTime.setText(sensorDataCaptureSession.getFormattedSessionStopTime());
            tvSessionStopTime.setText(sensorDataCaptureSession.getFormattedSessionStopTime());
            tvPriorMarketplacePosts = rootView.findViewById(R.id.tv_prior_posts_to_marketplace);
            tvPriorMarketplacePosts.setText(formatMarketplacePostHistory(sensorDataCaptureSession.getMarketplacePostHistory()));
            tvDownloadCount = rootView.findViewById(R.id.tv_download_count);
            tvDownloadCount.setText(Integer.toString(sensorDataCaptureSession.getPurchaseCount()));
        }

        etMarketplaceSeller = rootView.findViewById(R.id.et_marketplace_ds_seller);
        etMarketplaceSensorDescription = rootView.findViewById(R.id.et_marketplace_ds_peripheral_sensor);
        etMarketplaceSensorDescription.setText(sensorDataCaptureSession.getSensorName());
        etMarketplaceSessionDescription = rootView.findViewById(R.id.et_marketplace_ds_description);
        etMarketplaceSessionDescription.setText(sensorDataCaptureSession.getUserSessionDescription());
        etMarketplaceLatitude = rootView.findViewById(R.id.et_marketplace_ds_latitude);
        etMarketplaceLongitude = rootView.findViewById(R.id.et_marketplace_ds_longitude);
        etMarketplacePrice = rootView.findViewById(R.id.et_marketplace_ds_price);
        etMarketplaceIpAddress = rootView.findViewById(R.id.et_marketplace_ds_ip_address);
        etMarketplacePublicIpAddress = rootView.findViewById(R.id.et_marketplace_ds_public_ip_address);
        btnRegisterToMarketplace = rootView.findViewById(R.id.btn_register_to_marketplace);
        btnRegisterToMarketplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDdmAddressAndEthereumKeyAvailable()) {
                    if (allMarketplaceFieldsEntered()) {
                        if (GeneralUtils.isConnectedToNetwork(mContext)) {
                            submitJsonToIpfs();
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.check_network_connection), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.enter_all_marketplace_information), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.enter_ddm_and_ethereum), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.settings), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent startSettingsActivity = new Intent(mContext, SettingsActivity.class);
                                    startActivity(startSettingsActivity);
                                }
                            });
                    snackbar.show();
                }
            }
        });
        progressBar = rootView.findViewById(R.id.progressbar_register_to_marketplace);
        tvUserMessageContractResponse = rootView.findViewById(R.id.tv_user_message_contract_response);
        coordinatorLayout = rootView.findViewById(R.id.coordinatorLayout);

        ethereumPrivateKey = "";
        ddmContractAddressRopsten = "";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        return rootView;
    }

    private void submitJsonToIpfs() {

        // https://github.com/ipfs/java-ipfs-http-client requires android 26
        /*Thread ipfsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
                }
                catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });
        ipfsThread.start(); */

        // use infura HTTP API instead

        //convert input to JSON

        String marketplaceRecordJSON = marketplaceJsonFromInput();

        //write marketplaceRecordJSON to temporary file
        String filename = "marketplacerecord.json";
        FileOutputStream outputStream = null;

        tvUserMessageContractResponse.setText(getResources().getString(R.string.message_creating_file_from_sensor_data));
        try {
            outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(marketplaceRecordJSON.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.d(TAG, "file creation exception " + e.getMessage());
        }

        progressBar.setVisibility(View.VISIBLE);

        // direct POST to Infura IPFS "https://ipfs.infura.io:5001/api/v0/add?pin=false";
        final String BASE_URL = "https://ipfs.infura.io:5001/";
        Retrofit retrofit;

        //HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(httpLoggingInterceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitUploadJsonToIpfs uploadToIpfs = retrofit.create(RetrofitUploadJsonToIpfs.class);

        //Create a file object using file path
        final File file = new File(mContext.getFilesDir(), filename);

        tvUserMessageContractResponse.setText(getResources().getString(R.string.message_uploading_to_ipfs));

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file=", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        Call<InfuraIpfsResponse> call = uploadToIpfs.uploadAttachment2(filePart);

        call.enqueue(new Callback<InfuraIpfsResponse>() {
            @Override
            public void onResponse(Call<InfuraIpfsResponse> call, Response<InfuraIpfsResponse> response) {
                Log.d(TAG, "infura ipfs hash: " + response.body().getHash() + " length: "
                        + response.body().getHash().length());

                new RegisterProduct().execute(response.body().getHash());

                if (file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void onFailure(Call<InfuraIpfsResponse> call, Throwable t) {
                //Log.d(TAG, "Infura call error: " + t.getMessage());

                if (file.exists()) {
                    file.delete();
                }
            }
        });

    }

    private class RegisterProduct extends AsyncTask<String, String, String> {

        String userMessage;
        boolean successfullyPosted = false;

        @Override
        protected String doInBackground(String... strings) {

            userMessage = getResources().getString(R.string.message_posting_to_contract);
            publishProgress(userMessage);
            String ipfsHash = strings[0];

            // fields and formatting for smart contract "postProduct" function
            String hash_start;
            String hash_end;
            if (ipfsHash.length() == 46) {
                hash_start = ipfsHash.substring(0, 32);
                hash_end = ipfsHash.substring(32);
            } else {
                hash_start = ipfsHash;
                hash_end = "";
            }
            //convert to byte[] for smart contract
            byte[] hash_start_byte_array = hash_start.getBytes();
            byte[] hash_end_byte_array_pre = hash_end.getBytes();
            // increase hash_end_byte_array_pre array size to bytes32 to match smart contract
            byte[] hash_end_byte_array = new byte[32];
            for (int x=0; x<hash_end_byte_array.length;x++ ) {
                hash_end_byte_array[x] = 0;
            }
            System.arraycopy(hash_end_byte_array_pre, 0, hash_end_byte_array, 0, hash_end_byte_array_pre.length);

            String protocol_type = "1";
            byte[] protocol_type_byte_array = protocol_type.getBytes();

            // leave the following for "addProduct" functional and forks
            /*BigInteger hashfunction = new BigInteger(String.valueOf("20"));
            BigInteger size = new BigInteger(String.valueOf("0"));

            byte[] ipfsHashDecoded = decodeBase58(ipfsHash);
            byte[] ipfsHash_byte_array = ipfsHash.getBytes();
            String base58decoded = decodeBase58Old(ipfsHash);

            //byte[] ipfsHashDecoded = decodeBase58(ipfsHash);

            //String hexString = Hex.encodeHexString(ipfsHash_byte_array);
            //String hexString = new String(Hex.encodeHexString);
            String hexString = BaseEncoding.base16().encode(ipfsHash_byte_array);

            // decodeBase58
            byte[] base58byte = decodeBase58(ipfsHash);
            String base58byteToString = new String(base58byte);

            // decodeA
            byte[] base58Abyte = decodeA( ipfsHash );
            String base58AbyteToString = new String(base58Abyte);

            String base58AToHexString = bytesToHex(base58Abyte);
            String base58AToHexStringTrunc = base58AToHexString.substring(4); // lop off first two bytes/4 hex chars
            */

            // setup connection for web3
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            final String INFURA_WEB3_SERVER = "https://ropsten.infura.io/v3/c178e388bb3e4319a22b97a46332b071"; //using I3's key
            ethereumPrivateKey = sharedPreferences.getString(mContext.getResources().getString(R.string.settings_ethereum_private_key_key), "");
            ddmContractAddressRopsten = sharedPreferences.getString(mContext.getResources().getString(R.string.settings_ddm_contract_address_key), "");

            credentials = Credentials.create(ethereumPrivateKey);
            gasLimit = Contract.GAS_LIMIT;
            gasPrice = Contract.GAS_PRICE;

            Web3j web3j = Web3jFactory.build(new HttpService(INFURA_WEB3_SERVER));
            try {
                Log.d(TAG, "Connected to web3j, client version: "
                        + web3j.web3ClientVersion().sendAsync().get().getWeb3ClientVersion());
            } catch (InterruptedException e) {
                e.printStackTrace();
                userMessage = e.toString();
            } catch (ExecutionException e) {
                e.printStackTrace();
                userMessage = e.toString();
            }

            DDMV2Contract ddmv2Contract = DDMV2Contract.load(ddmContractAddressRopsten, web3j, credentials, gasPrice, gasLimit);
            //kddmv2Contract.isValid();

            try {

                // this intentionally calls postProduct function, patterned after DDM marketplace's
                // use of event instead of contract to provide list of data products
                TransactionReceipt transactionReceipt = ddmv2Contract
                        .postProduct(hash_start_byte_array, hash_end_byte_array, protocol_type_byte_array)
                        .send();
                /*TransactionReceipt transactionReceipt = ddmv2Contract
                        .addProduct(ipfsHash_byte_array, hashfunction, size)
                        .send();*/
                userMessage = getResources().getString(R.string.message_successfully_posted);

                // update sensorDataCaptureSession record
                sensorDataCaptureSession.appendToMarketplacePostHistory(Long.toString(System.currentTimeMillis()));
                mDb = AppDatabase.getInstance(mContext);
                mDb.sensorDataCaptureSessionDao().updateSensorDataCaptureSession(sensorDataCaptureSession);
                successfullyPosted = true;

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "error in post product: " + e.getMessage());
                userMessage = "Error: " + e.getMessage();
            } finally {
                // web3j.shutdown(); //command unavailable
            }

            return userMessage;
        }

        @Override
        protected void onProgressUpdate(String... text) {

            tvUserMessageContractResponse.setText(text[0]);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            tvUserMessageContractResponse.setText(userMessage);

            if (successfullyPosted) {
                tvPriorMarketplacePosts.setText(formatMarketplacePostHistory(sensorDataCaptureSession.getMarketplacePostHistory()));
            }

        }
    }

    // https://github.com/web3j/web3j/blob/master/crypto/src/test/java/org/web3j/crypto/Base58.java
    public static byte[] decodeBase58(String input)  {

        final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
        final char ENCODED_ZERO = ALPHABET[0];
        final int[] INDEXES = new int[128];

        if (input.length() == 0) {
            return new byte[0];
        }
        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
        byte[] input58 = new byte[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            int digit = c < 128 ? INDEXES[c] : -1;
            if (digit < 0) {
                //throw new AddressFormatException.InvalidCharacter(c, i);
            }
            input58[i] = (byte) digit;
        }
        // Count leading zeros.
        int zeros = 0;
        while (zeros < input58.length && input58[zeros] == 0) {
            ++zeros;
        }
        // Convert base-58 digits to base-256 digits.
        byte[] decoded = new byte[input.length()];
        int outputStart = decoded.length;
        for (int inputStart = zeros; inputStart < input58.length; ) {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
            if (input58[inputStart] == 0) {
                ++inputStart; // optimization - skip leading zeros
            }
        }
        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.length && decoded[outputStart] == 0) {
            ++outputStart;
        }
        // Return decoded data (including original number of leading zeros).
        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
    }

    private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        // this is just long division which accounts for the base of the input digits
        int remainder = 0;
        for (int i = firstDigit; i < number.length; i++) {
            int digit = (int) number[i] & 0xFF;
            int temp = remainder * base + digit;
            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }
        return (byte) remainder;
    }

    public String decodeBase58Old(String str) {

        final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
        final char ENCODED_ZERO = ALPHABET[0];

        Log.d(TAG, "hash input string: " + str);
        int decimal = 0;

        // restore decimal.
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char temp_c = chars[i];

            int index_num = 0;
            for (int j = 0; j < ALPHABET.length; j++) {
                if (ALPHABET[j] == temp_c) {
                    index_num = j;
                }
            }

            decimal = decimal * 58;
            decimal = decimal + index_num;
        }

        // Decimal → Hex
        Log.d(TAG, "hash decodeBase58Old to decimal: " + Integer.toString(decimal));
        String s_hex = Integer.toHexString((int) decimal);
        Log.d(TAG, "hash decodeBase58Old decimal to hex: " + s_hex);

        return new String(s_hex);

        // Hex → string

        /*byte[] bytes = DatatypeConverter.parseHexBinary(s_hex);
        return new String(bytes);*/
    }

    public static byte[] decodeA(String input) /*throws AddressFormatException */ {

        final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        final BigInteger BASE = BigInteger.valueOf(58);

        byte[] bytes = decodeToBigInteger(input).toByteArray();
        // We may have got one more byte than we wanted, if the high bit of the next-to-last byte was not zero. This
        // is because BigIntegers are represented with twos-compliment notation, thus if the high bit of the last
        // byte happens to be 1 another 8 zero bits will be added to ensure the number parses as positive. Detect
        // that case here and chop it off.
        boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;
        // Count the leading zeros, if any.
        int leadingZeros = 0;
        for (int i = 0; input.charAt(i) == ALPHABET.charAt(0); i++) {
            leadingZeros++;
        }
        // Now cut/pad correctly. Java 6 has a convenience for this, but Android can't use it.
        byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
        System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
        return tmp;
    }

    public static BigInteger decodeToBigInteger(String input) /*throws AddressFormatException */ {

        final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        final BigInteger BASE = BigInteger.valueOf(58);

        BigInteger bi = BigInteger.valueOf(0);
        // Work backwards through the string.
        for (int i = input.length() - 1; i >= 0; i--) {
            int alphaIndex = ALPHABET.indexOf(input.charAt(i));
            if (alphaIndex == -1) {
                //throw new AddressFormatException("Illegal character " + input.charAt(i) + " at " + i);
                Log.d("hash", "hash decode to big integer throw");
            }
            bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(BASE.pow(input.length() - 1 - i)));
        }
        return bi;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private boolean allMarketplaceFieldsEntered() {
        boolean marketplaceFieldsEntered;

        if (etMarketplaceSeller.getText().toString().trim().equals("") || etMarketplaceSensorDescription.getText().toString().trim().equals("")
                || etMarketplaceSessionDescription.getText().toString().trim().equals("") || etMarketplacePrice.getText().toString().trim().equals("")
                || etMarketplaceIpAddress.getText().toString().trim().equals("") || etMarketplacePublicIpAddress.getText().toString().trim().equals("")) {
            marketplaceFieldsEntered = false;
        } else {
            marketplaceFieldsEntered = true;
        }

        return marketplaceFieldsEntered;
    }

    private boolean isDdmAddressAndEthereumKeyAvailable() {
        boolean ddmAddressAndEthereumKeyAvailable;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        ethereumPrivateKey = sharedPreferences.getString(mContext.getResources().getString(R.string.settings_ethereum_private_key_key), "");
        ddmContractAddressRopsten = sharedPreferences.getString(mContext.getResources().getString(R.string.settings_ddm_contract_address_key), "");

        if (ddmContractAddressRopsten.trim().equals("") || ethereumPrivateKey.trim().equals("")) {
            ddmAddressAndEthereumKeyAvailable = false;
        } else {
            ddmAddressAndEthereumKeyAvailable = true;
        }

        return ddmAddressAndEthereumKeyAvailable;
    }

    private String formatMarketplacePostHistory(String marketplacePostHistory) {
        String formattedMarketplacePostHistory = "";
        if (marketplacePostHistory != null && !marketplacePostHistory.trim().equals("")) {
            String[] marketplacePostHistoryArray = marketplacePostHistory.split(",");
            for (String marketplacePost : marketplacePostHistoryArray) {
                if (marketplacePost !=null && !marketplacePost.trim().equals("")) {
                    try {
                        formattedMarketplacePostHistory += GeneralUtils.getFormattedDateFromMillis(Long.parseLong(marketplacePost)) + "\n";
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }
        }
        if (!formattedMarketplacePostHistory.equals("")) {
            return formattedMarketplacePostHistory;
        } else {
            return getResources().getString(R.string.none);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_ddm_contract_address_key))) {
            ddmContractAddressRopsten = sharedPreferences.getString(key, "");
        } else if (key.equals(getString(R.string.settings_ethereum_private_key_key))) {
            ethereumPrivateKey = sharedPreferences.getString(key, "");
        }
    }

    private String marketplaceJsonFromInput() {

        //return Json object that matches record used in DDM at QueryProduct.js and RegistrationForm.js

        JSONObject json = new JSONObject();
        try {
            json.put("Seller", etMarketplaceSeller.getText().toString().trim());
            json.put("Peripheral_Sensor", etMarketplaceSensorDescription.getText().toString().trim());
            json.put("Product_Description", etMarketplaceSessionDescription.getText().toString().trim());
            json.put("Price_In_USD", etMarketplacePrice.getText().toString().trim());
            json.put("Max_Data_Unit", etMarketplacePrice.getText().toString().trim());

            json.put("Longitude", etMarketplaceLongitude.getText().toString().trim());
            json.put("Latitude", etMarketplaceLatitude.getText().toString().trim());
            json.put("Price_per_Data_Unit_USD", etMarketplacePrice.getText().toString().trim());
            json.put("Data_Unit", ""); //
            json.put("IP_Address", etMarketplaceIpAddress.getText().toString().trim());
            json.put("Public_Address", etMarketplacePublicIpAddress.getText().toString().trim());
            json.put("Seller_Credentials", ""); //

            return json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_session_detail, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch ( id) {

            case R.id.action_delete_session: {
                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getResources().getString(R.string.session_delete_confirmation_title) )
                        .setMessage(mContext.getResources().getString(R.string.session_delete_confirmation_message)  )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                mDb = AppDatabase.getInstance(mContext);
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDb.sensorDataDao().deleteSensorDataBySessionId(sensorDataCaptureSession.getSessionId());
                                        mDb.sensorDataCaptureSessionDao().deleteSensorDataCaptureSessionBySessionId(sensorDataCaptureSession.getSessionId());
                                    }
                                });

                                Toast.makeText(mContext, mContext.getResources().getString(R.string.session_deleted_message), Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(mContext).unregisterOnSharedPreferenceChangeListener(this);
    }
}
