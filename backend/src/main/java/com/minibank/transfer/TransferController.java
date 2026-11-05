package com.minibank.transfer;

import com.minibank.api.TransfersApi;
import com.minibank.api.model.TransferRequest;
import com.minibank.api.model.TransferResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/** Implémente POST /transfers (interface générée depuis openapi.yaml). */
@RestController
public class TransferController implements TransfersApi {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @Override
    public ResponseEntity<TransferResult> createTransfer(TransferRequest transferRequest) {
        // 201 Created : un POST qui crée une ressource
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.createTransfer(transferRequest));
    }
}
