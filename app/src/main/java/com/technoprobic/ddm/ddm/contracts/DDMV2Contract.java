package com.technoprobic.ddm.ddm.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class DDMV2Contract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610286806100206000396000f3fe608060405234801561001057600080fd5b5060043610610052577c010000000000000000000000000000000000000000000000000000000060003504632311dc0b811461005757806394e08fc2146100b4575b600080fd5b6100a26004803603606081101561006d57600080fd5b50803590602081013590604001357fff00000000000000000000000000000000000000000000000000000000000000166100e4565b60408051918252519081900360200190f35b6100e2600480360360608110156100ca57600080fd5b5080359060ff60208201358116916040013516610157565b005b60408051338152602081018590528082018490527fff000000000000000000000000000000000000000000000000000000000000008316606082015290516000917f82b783fdc9b47f1686078fb8529f295a9a04a665af43be12db8b03d4394f7034919081900360800190a19392505050565b61015f61023a565b506040805160608101825293845260ff928316602085019081529183169084019081526001805480820182556000828152955160029182027fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf6810182905594517fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf79095018054945187166101000261ff00199690971660ff1990951694909417949094169490941790915582549081018355919092527f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ace0155565b60408051606081018252600080825260208201819052918101919091529056fea165627a7a72305820f94d6258c421b445f9d9b12b2d5061c3c266a507ed3a444675d1a02dbedef9af0029";

    protected DDMV2Contract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DDMV2Contract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<PostProductsEventResponse> getPostProductsEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PostProducts", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes1>() {}));
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(event, transactionReceipt);
        ArrayList<PostProductsEventResponse> responses = new ArrayList<PostProductsEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            PostProductsEventResponse typedResponse = new PostProductsEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.indexed_from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.hash_start = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.hash_end = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.ptype = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PostProductsEventResponse> postProductsEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PostProducts", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes1>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PostProductsEventResponse>() {
            @Override
            public PostProductsEventResponse call(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(event, log);
                PostProductsEventResponse typedResponse = new PostProductsEventResponse();
                typedResponse.log = log;
                typedResponse.indexed_from = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.hash_start = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.hash_end = (byte[]) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.ptype = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> postProduct(byte[] _hash_start, byte[] _hash_end, byte[] _pytpe) {
        final Function function = new Function(
                "postProduct", 
                Arrays.<Type>asList(new Bytes32(_hash_start),
                new Bytes32(_hash_end),
                new Bytes1(_pytpe)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addProduct(byte[] _hash, BigInteger _hashfunction, BigInteger _size) {
        final Function function = new Function(
                "addProduct", 
                Arrays.<Type>asList(new Bytes32(_hash),
                new org.web3j.abi.datatypes.generated.Uint8(_hashfunction), 
                new org.web3j.abi.datatypes.generated.Uint8(_size)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<DDMV2Contract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DDMV2Contract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DDMV2Contract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DDMV2Contract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static DDMV2Contract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DDMV2Contract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static DDMV2Contract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DDMV2Contract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PostProductsEventResponse {
        public Log log;

        public String indexed_from;

        public byte[] hash_start;

        public byte[] hash_end;

        public byte[] ptype;
    }
}
