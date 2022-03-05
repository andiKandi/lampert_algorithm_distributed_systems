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

import java.io.Serializable;
import java.util.Objects;

public class Event implements Serializable, Comparable<Event> {

    private final int pid; //process identifier
    private final int clock; //clock value of sending process when sent
    private final int size; //payload

    private String type = "REQUEST"; //request, acknowledge
    private int missingAcks; //count of acknowledgements that are still missing before Transmission to datastore can be made.

    private Event(int pid, int clock, int size) {
        this.pid = pid;
        this.clock = clock;
        this.size = size;
    }

    public static Event eventGenerator(int pid, int clockCountWhenCreated, int numberOfDatasources, int size) {
        final Event event = new Event(pid, clockCountWhenCreated, size);
        event.missingAcks = numberOfDatasources;
        return event;
    }

    public int getPid() {
        return pid;
    }

    public int getSize() {
        return size;
    }

    public int getClock() {
        return clock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMissingAcks() {
        return missingAcks;
    }

    public void lowerMissingAcks() {
        --this.missingAcks;
    }

    //for sorting in lists;
    //based on logical clock counter and priority in case of equal clock counter
    @Override
    public int compareTo(Event that) {
        if (this.clock > that.getClock()) {
            return 1;
        } else if (this.clock < that.getClock()) {
            return -1;
        } else {
            // Break ties with process id (pid) for datasource;
            if (this.getPid() > that.getPid()) {
                return 1;
            } else if (this.getPid() < that.getPid()) {
                return -1;
            } else {
                // PID SHOULD NEVER BE EQUAL
                return 0;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return getPid() == event.getPid() && getClock() == event.getClock() && Objects.equals(getType(), event.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPid(), getClock(), getType(), getMissingAcks());
    }

    @Override
    public String toString() {
        return String.format(this.pid + ">" + this.clock + ">" + this.type + ">" + this.missingAcks + ">" + this.size);
    }
}
