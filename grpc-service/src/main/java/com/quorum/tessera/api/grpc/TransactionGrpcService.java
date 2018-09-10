package com.quorum.tessera.api.grpc;

import com.google.protobuf.ByteString;
import com.quorum.tessera.enclave.EnclaveMediator;
import com.quorum.tessera.api.grpc.model.*;
import io.grpc.stub.StreamObserver;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class TransactionGrpcService extends TransactionGrpc.TransactionImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionGrpcService.class);


    private final EnclaveMediator enclaveMediator;

    public TransactionGrpcService(EnclaveMediator enclaveMediator) {
        this.enclaveMediator = Objects.requireNonNull(enclaveMediator);
    }

    @Override
    public void send(SendRequest grpcSendRequest, StreamObserver<SendResponse> responseObserver) {

        com.quorum.tessera.api.model.SendRequest sendRequest = Convertor.toModel(grpcSendRequest);
        com.quorum.tessera.api.model.SendResponse response = enclaveMediator.send(sendRequest);

        final SendResponse grpcResponse = Convertor.toGrpc(response);

        responseObserver.onNext(grpcResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void receive(ReceiveRequest grpcRequest, StreamObserver<ReceiveResponse> responseObserver) {

        com.quorum.tessera.api.model.ReceiveRequest request = Convertor.toModel(grpcRequest);

        String encodedPayload = enclaveMediator.receiveAndEncode(request);

        final ReceiveResponse response = ReceiveResponse
                .newBuilder()
                .setPayload(encodedPayload)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void delete(DeleteRequest grpcRequest, StreamObserver<DeleteRequest> responseObserver) {
        LOGGER.debug("Received delete key request");

        com.quorum.tessera.api.model.DeleteRequest request = Convertor.toModel(grpcRequest);

        enclaveMediator.delete(request);
        
        responseObserver.onNext(grpcRequest);
        responseObserver.onCompleted();
    }

    @Override
    public void resend(ResendRequest grpcRequest, StreamObserver<ResendResponse> responseObserver) {
        LOGGER.debug("Received resend request");
        
        com.quorum.tessera.api.model.ResendRequest request = Convertor.toModel(grpcRequest);
        Optional<byte[]> result = enclaveMediator.resendAndEncode(request);
        ResendResponse.Builder builder = ResendResponse.newBuilder();
        result.map(ByteString::copyFrom).ifPresent(builder::setData);
        
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();

    }

    @Override
    public void push(PushRequest request, StreamObserver<PushRequest> responseObserver) {
        LOGGER.debug("Received push request");

        enclaveMediator.storePayload(request.toByteArray());
        

        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }
}
