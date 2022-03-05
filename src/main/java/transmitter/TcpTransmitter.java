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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.Event;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TcpTransmitter {

    private static final Logger logger = LogManager.getLogger(TcpTransmitter.class);

    // tcp transmitter method for transmitting events between datasources
    public static void transmit(final String host, final int port, final Event event) {
        try {
            logger.trace("sending event '{}' to {}:{}", event, host, port);
            final Socket socket = new Socket(host, port);
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(event);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (final IOException e) {
            logger.error("failed to transmit event '{}' to host {}:{}", event, host, port, e);
        }
    }
}
