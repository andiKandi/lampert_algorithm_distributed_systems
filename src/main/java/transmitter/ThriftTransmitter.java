/**
 * Copyright (C) 2021 Kraus Andreas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package transmitter;

import de.hda.fbi.ds.mbredel.exam.thrift.Resource;
import de.hda.fbi.ds.mbredel.exam.thrift.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class ThriftTransmitter {

    private static final Logger logger = LogManager.getLogger(ThriftTransmitter.class);

    private final String host;
    private final int port;

    public ThriftTransmitter(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    // Thrift transmitter for persisting events in datastore
    public void persist(final String name, final int size) {
        logger.trace("RPC sending name: {}, size: {}", name, size);

        final TTransport transport = new TSocket(this.host, this.port, 0, 0);
        try {
            transport.open();
            final TProtocol protocol = new TBinaryProtocol(transport);
            final Service.Client client = new Service.Client(protocol);
            client.storeData(new Resource(name, size));
        } catch (final TException e) {
            logger.error("unable to persist name: {}, size: {}", name, size, e);
        }

        if (transport.isOpen()) {
            transport.close();
        }
    }
}