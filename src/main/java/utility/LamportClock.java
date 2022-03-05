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

public class LamportClock {

    private static final int ARBITRARY_INCREMENT_COUNT = 1;

    private int clock = 0;

    public LamportClock() {
    }

    /**
     * incrementation of clock due to local process event
     */
    public synchronized void localEvent() {
        clock += ARBITRARY_INCREMENT_COUNT;
    }

    /**
     * synchronizing local clock with other clock and increasing logical clock count
     * due to message receive process from other node=datasource
     * @param messageClock
     */
    public synchronized void messageEvent(int messageClock) {
        clock = Math.max(messageClock, this.clock) + ARBITRARY_INCREMENT_COUNT;
    }

    public int getClock() {
        return clock;
    }
}
