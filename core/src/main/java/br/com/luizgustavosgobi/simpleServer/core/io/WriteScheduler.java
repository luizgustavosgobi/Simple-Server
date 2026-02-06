package br.com.luizgustavosgobi.simpleServer.core.io;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;

public interface WriteScheduler {
    void scheduleWrite(Client client);
    void cancelWriteSchedule(Client client);
}
