/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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

package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.VMException;
import me.itzsomebody.vm.datatypes.JWrapper;

public class Dup extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        switch ((Integer) operands[0]) {
            case 0: { // DUP
                JWrapper value = vm.pop();
                vm.push(value);
                vm.push(value);
                break;
            }
            case 1: { // DUP_X1
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                vm.push(first);
                vm.push(second);
                vm.push(first);
                break;
            }
            case 2: { // DUP_X2
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                JWrapper third = vm.pop();
                vm.push(first);
                vm.push(second);
                vm.push(third);
                vm.push(first);
                break;
            }
            case 3: { // DUP2
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                vm.push(first);
                vm.push(second);
                vm.push(first);
                vm.push(second);
                break;
            }
            case 4: { // DUP2_X1
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                JWrapper third = vm.pop();
                vm.push(second);
                vm.push(first);
                vm.push(third);
                vm.push(second);
                vm.push(first);
                break;
            }
            case 5: { // DUP2_X2
                JWrapper first = vm.pop();
                JWrapper second = vm.pop();
                JWrapper third = vm.pop();
                JWrapper fourth = vm.pop();
                vm.push(second);
                vm.push(first);
                vm.push(fourth);
                vm.push(third);
                vm.push(second);
                vm.push(first);
                break;
            }
            default:
                throw new VMException();
        }
    }
}
