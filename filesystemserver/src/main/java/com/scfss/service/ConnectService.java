package com.scfss.service;

import com.scfss.dto.ConnectResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConnectService {

    //    Each client can hold multiple connections.
    //    They will be given unique ids
    public ConnectResponse connect() {
        UUID uuid = UUID.randomUUID();
        ConnectResponse connectResponse = new ConnectResponse();
        connectResponse.setUuid(uuid);
        return connectResponse;
    }
}
