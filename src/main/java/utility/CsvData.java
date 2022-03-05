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

public class CsvData {
    private final int waitingTime;
    private final int size;

    public CsvData(final int waitingTime, final int size) {
        this.waitingTime = waitingTime;
        this.size = size;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getSize() {
        return size;
    }
}
