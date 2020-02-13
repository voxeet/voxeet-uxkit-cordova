/*
 *
 * VoxeetUXKit Cordova
 * Copyright (C) 2020
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/

export interface ActionBar {
    displayMute?: boolean,
    displaySpeaker?: boolean,
    displayCamera?: boolean,
    displayScreenShare?: boolean,
    displayLeave?: boolean
}

export interface Overlay {
    backgroundMaximizedColor?: number,
    backgroundMinimizedColor?: number
}

export interface Users {
    speakingUserColor?: number,
    selectedUserColor?: number
}

export interface Configuration {
    actionBar?: ActionBar,
    overlay?: Overlay,
    users?: Users
}