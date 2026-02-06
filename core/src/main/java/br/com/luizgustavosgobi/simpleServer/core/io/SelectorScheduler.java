package br.com.luizgustavosgobi.simpleServer.core.io;

import br.com.luizgustavosgobi.simpleServer.core.connection.Client;
import br.com.luizgustavosgobi.simpleServer.core.logger.Logger;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class SelectorScheduler implements WriteScheduler {

    private final Selector selector;

    public SelectorScheduler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void scheduleWrite(Client client) {
        SelectionKey key = client.getSelectionKey();
        if (key != null && key.isValid()) {
            try {
                key.interestOpsOr(SelectionKey.OP_WRITE);
            } catch (Exception e) {
                Logger.Error("Error while trying to schedule a Write interest for the client " + client.getAddress());
            }
        }

        selector.wakeup();
    }

    @Override
    public void cancelWriteSchedule(Client client) {
        SelectionKey key = client.getSelectionKey();
        if (key != null && key.isValid()) {
            try {
                key.interestOpsAnd(~SelectionKey.OP_WRITE);
            } catch (Exception e) {
                Logger.Error("Error while trying remove the Write interest for the client " + client.getAddress());
            }
        }

        selector.wakeup();
    }
}
