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
package utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.SortedSet;

public class DataSourceListener extends Thread {

    private static final Logger logger = LogManager.getLogger(DataSourceListener.class);

    private final LamportClock lamportClock;
    private final SortedSet<Event> eventList;
    private final int port;

    public DataSourceListener(LamportClock lamportClock, SortedSet<Event> eventList, int port) {
        this.lamportClock = lamportClock;
        this.eventList = eventList;
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                try {
                    Event event = (Event) objectInputStream.readObject();
                    switch (event.getType()) {
                        case "REQUEST":
                            // incoming request is added to own eventList and synchronizing + increasing the logical clock count
                            logger.trace("received request event '{}', (current list: {})", event, eventList);
                            eventList.add(event);
                            lamportClock.messageEvent(event.getClock());
                            break;
                        case "ACKNOWLEDGE":
                            // incoming request acknowledgement is used to lower missingAcks counter of own current sending
                            // event and increasing the logical clock count
                            logger.trace("received acknowledgement event '{}', (current list: {})", event, eventList);
                            eventList.first().lowerMissingAcks();
                            lamportClock.localEvent();
                            break;
                        default:
                            logger.warn("received event of unknown type '{}'", event);
                            break;
                    }
                } catch (ClassNotFoundException ex) {
                    logger.error("could not read object", ex);
                }
            }
        } catch (IOException ex) {
            logger.error("could not start the listener", ex);
        }
    }
}