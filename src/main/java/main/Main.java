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

package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import transmitter.TcpTransmitter;
import transmitter.ThriftTransmitter;
import utility.*;

import java.util.*;

public class Main {

    /**
     * Logger instead of system.out.println
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {

        /*
         * Parsing of cmdline args
         */
        final CommandLineArguments arguments = new CommandLineArguments();
        final CmdLineParser parser = new CmdLineParser(arguments);
        try {
            parser.parseArgument(args);
            if (arguments.isPrintHelpMessage()) {
                parser.printUsage(System.out);
                System.exit(0);
            }
        } catch (final CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }

        ThriftTransmitter thriftTransmitter = new ThriftTransmitter(arguments.getDatastoreHost(), arguments.getDatastorePort()); //transmitter for persisting data in datastore via Thrift
        LamportClock lamportClock = new LamportClock(); //initialization of the local clock
        SortedSet<Event> eventList = Collections.synchronizedSortedSet(new TreeSet<>()); //Threadsafe list for all events
        List<String> datasources = arguments.getDatasources(); //list of all datasources
        final String datasourceName = arguments.getDatasources().get(arguments.getOwnPid()); //name of this datasource


        /*
         * 1. sequential: Read csv file and thus get waiting time between messages and message data
         */
        ArrayList<CsvData> csvData = CsvReader.readCsv(arguments.getCsvFile());

        /*
         * 2a. listener runs parallel to eventGeneration and eventHandling: Receiving either REQUEST, ACKNOWLEDGE
         */
        Thread listener = new Thread(new DataSourceListener(lamportClock, eventList, arguments.getDatasourcePort()));
        listener.start();

        // wait for all containers to become available (DNS hostname might be unknown if the container is not yet started)
        Thread.sleep(5000l);

        /*
         * 2b. eventGeneration runs parallel to listener and eventHandling: fill processing queue with events
         */
        new Thread() {
            @Override
            public void run() {
                //puts data into list that is tha basis of future events
                final Iterator<CsvData> iterator = csvData.iterator();

                // Case: There are still own csvData-packages left, that will create events
                while (iterator.hasNext()) {
                    try {
                        // Case: currently there is no other own event in eventList
                        if (!eventList.stream().anyMatch((event) -> event.getPid() == arguments.getOwnPid())) {
                            CsvData data = iterator.next();
                            // sleep time according to read in csvData
                            Thread.sleep(data.getWaitingTime() * 1000L);

                            // adding event to eventlist and increasing the logical clock count
                            Event event = Event.eventGenerator(arguments.getOwnPid(), lamportClock.getClock(), (datasources.size() - 1), data.getSize());//generation of an event
                            eventList.add(event);
                            lamportClock.localEvent();

                            // sending request message to all other datasources and increasing the logical clock count
                            for (int j = 0; j < datasources.size(); ++j) {
                                if (j != event.getPid()) {
                                    TcpTransmitter.transmit(datasources.get(j), arguments.getDatasourcePort(), event);
                                }
                            }
                            lamportClock.localEvent();
                        } else {
                            // wait for 100 ms to cool cpu load
                            Thread.sleep(100L);
                        }
                    } catch (final InterruptedException e) {
                        logger.error("failed to sleep and wait for next execution", e);
                    }
                }
                logger.info("no more data available");
            }
        }.start();


        /*
         * 2c. eventHandling runs parallel to listener and eventGeneration: main logic for processing events in the eventList
         */
        while (true) {
            // Case: This datasource's own event is the first in line in the eventList AND ALL other datasources acknowledged the request to persist data
            if (!eventList.isEmpty() && eventList.first().getPid() == arguments.getOwnPid() && eventList.first().getMissingAcks() == 0) {

                //sending resource to datastore and increasing the logical clock count
                thriftTransmitter.persist(datasourceName, eventList.first().getSize());
                lamportClock.localEvent();

                //removal of own event that is now done and increasing the logical clock count
                eventList.remove(eventList.first());
                lamportClock.localEvent();
            }

            // Case: This datasource's own event is the first in line in the eventList BUT NOT ALL other datasources acknowledged the request to persist data
            if (!eventList.isEmpty() && eventList.first().getPid() == arguments.getOwnPid() && eventList.first().getMissingAcks() != 0) {
                Thread.sleep(100L); // wait for 100ms until next iteration (to cool cpu load)
                continue;
            }

            // Case: The first event in the eventList belongs to a different datasource
            if (!eventList.isEmpty() && eventList.first().getPid() != arguments.getOwnPid()) {

                //acknowledgement of other datasource's sending request and increasing the logical clock count
                eventList.first().setType("ACKNOWLEDGE");
                String datasourceOfCurrentEvent = datasources.get(eventList.first().getPid());
                TcpTransmitter.transmit(datasourceOfCurrentEvent, arguments.getDatasourcePort(), eventList.first());
                lamportClock.localEvent();

                //removal of other datasource's sending request that was just acknowledged and increasing the logical clock count
                eventList.remove(eventList.first());
                lamportClock.localEvent();
            }
        }
    }
}