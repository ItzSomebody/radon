/*
 * Copyright (C) 2018 ItzSomebody
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.templates;

class SuperLightStringEncryption {
    public static String decrypt(String encrypted, int key) {
        char[] encryptedArray = encrypted.toCharArray();
        char[] returnThis = new char[encryptedArray.length];

        for (int i = 0; i < returnThis.length; i++) {
            returnThis[i] = (char) (encryptedArray[i] ^ key);
        }

        return new String(returnThis);
    }
}
