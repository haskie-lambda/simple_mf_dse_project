/*
 * Copyright (C) 2020 Fabian Schneider<a11709041@unet.univie.ac.at>
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.Team107.MF.functionalbasics;

/**
 * Haskells a -> ()
 * (mainly useful in effect systems)
 *
 * @author Fabian Schneider
 */
public interface UnaryHandler<X> {
    public void run(X x);
}
