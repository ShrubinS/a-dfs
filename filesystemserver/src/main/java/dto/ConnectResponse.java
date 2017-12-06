package dto;

import java.io.Serializable;
import java.util.UUID;

public class ConnectResponse implements Serializable {
    UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
