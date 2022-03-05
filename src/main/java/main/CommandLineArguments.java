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

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandLineArguments {

    @Option(name = "-d", aliases = {"--datasources"}, usage = "the ip or servernames of the other datasources", handler = StringArrayOptionHandler.class)
    private List<String> datasources = new ArrayList<>(); //like in sh file

    @Option(name = "-u", aliases = {"--datasourcePort"}, usage = "the port of the data source")
    private int datasourcePort = 25000;

    @Option(name = "-i", aliases = {"--datastoreHost"}, usage = "the ip or servername of the datastore", required = true)
    private String datastoreHost = "datastoreHost";

    @Option(name = "-p", aliases = {"--datastorePort"}, usage = "the port of the datastore")
    private int datastorePort = 9090;

    @Option(name = "-h", aliases = {"--help"}, help = true)
    private boolean printHelpMessage = false;

    @Option(name = "-f", aliases = {"--file"}, usage = "the CSV file that gives send intervals and message content")
    private File csvFile;

    @Option(name = "-q", aliases = {"--pid"}, usage = "the process ID", required = true)
    private int pid;

    public boolean isPrintHelpMessage() {
        return printHelpMessage;
    }

    public List<String> getDatasources() {
        return datasources;
    }

    public String getDatastoreHost() {
        return datastoreHost;
    }

    public int getOwnPid() {
        return pid;
    }

    public int getDatastorePort() {
        return datastorePort;
    }

    public int getDatasourcePort() { return datasourcePort; }

    public File getCsvFile() {
        return csvFile;
    }

}
